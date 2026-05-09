package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import org.apache.poi.xwpf.usermodel.*;
import com.houserental.entity.ElectronicSignature;
import com.houserental.entity.House;
import com.houserental.entity.LeaseAgreement;
import com.houserental.entity.User;
import com.houserental.mapper.BillMapper;
import com.houserental.mapper.ElectronicSignatureMapper;
import com.houserental.mapper.HouseMapper;
import com.houserental.mapper.LeaseAgreementMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.FileService;
import com.houserental.service.LeaseAgreementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 租约服务实现
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement> implements LeaseAgreementService {

    private static final Logger log = LoggerFactory.getLogger(LeaseAgreementServiceImpl.class);

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ElectronicSignatureMapper electronicSignatureMapper;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private com.houserental.service.WalletService walletService;

    @Override
    @Transactional
    public Long createLease(LeaseAgreement lease) {
        if (lease.getHouseId() == null) {
            throw new com.houserental.common.exception.BusinessException("房源ID不能为空");
        }

        House house = houseMapper.selectById(lease.getHouseId());
        if (house == null) {
            throw new com.houserental.common.exception.BusinessException("房源不存在");
        }

        if (house.getStatus() != null && house.getStatus() == 1) {
            throw new com.houserental.common.exception.BusinessException("该房源已出租，无法重复发起合同");
        }

        QueryWrapper<LeaseAgreement> leaseWrapper = new QueryWrapper<>();
        leaseWrapper.eq("house_id", lease.getHouseId());
        leaseWrapper.in("status", 0, 1, 2);
        long activeLeaseCount = baseMapper.selectCount(leaseWrapper);
        if (activeLeaseCount > 0) {
            throw new com.houserental.common.exception.BusinessException("该房源已有生效合同，无法重复发起");
        }

        lease.setLeaseNo("LEASE" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        lease.setCreateTime(LocalDateTime.now());
        lease.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(lease);

        createSignatureRecords(lease);

        return lease.getId();
    }

    private void createSignatureRecords(LeaseAgreement lease) {
        // 创建租客签章记录
        ElectronicSignature tenantSignature = new ElectronicSignature();
        tenantSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        tenantSignature.setAgreementId(lease.getId());
        tenantSignature.setUserId(lease.getTenantId());
        tenantSignature.setUserType("TENANT");
        tenantSignature.setStatus(0); // 待签署
        tenantSignature.setCreateTime(LocalDateTime.now());
        tenantSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(tenantSignature);

        // 创建房东签章记录
        ElectronicSignature landlordSignature = new ElectronicSignature();
        landlordSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        landlordSignature.setAgreementId(lease.getId());
        landlordSignature.setUserId(lease.getLandlordId());
        landlordSignature.setUserType("LANDLORD");
        landlordSignature.setStatus(0); // 待签署
        landlordSignature.setCreateTime(LocalDateTime.now());
        landlordSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(landlordSignature);
    }

    @Override
    @Transactional
    public boolean signLease(Long id, Long userId, String userType, String signatureData) {
        log.info("开始签署合同 leaseId={}, userId={}, userType={}", id, userId, userType);
        
        QueryWrapper<LeaseAgreement> lockWrapper = new QueryWrapper<>();
        lockWrapper.eq("id", id).last("FOR UPDATE");
        LeaseAgreement lease = baseMapper.selectOne(lockWrapper);
        if (lease == null) {
            log.error("合同不存在 leaseId={}", id);
            return false;
        }
        log.info("找到合同 leaseId={}, 当前状态={}", id, lease.getStatus());

        // 如果没有传 userType，通过合同自动判断
        if (userType == null || userType.isEmpty()) {
            if (userId.equals(lease.getTenantId())) {
                userType = "TENANT";
                log.info("自动判断用户类型: TENANT (租客)");
            } else if (userId.equals(lease.getLandlordId())) {
                userType = "LANDLORD";
                log.info("自动判断用户类型: LANDLORD (房东)");
            } else {
                log.error("用户{}不是合同相关方，无法签署", userId);
                return false;
            }
        }

        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("agreement_id", id);
        wrapper.eq("user_id", userId);
        wrapper.eq("user_type", userType);
        ElectronicSignature signature = electronicSignatureMapper.selectOne(wrapper);
        log.info("找到签名记录: {}", signature);
        
        if (signature != null && signature.getStatus() == 0) {
            signature.setSignatureData(signatureData);
            signature.setSigningTime(new java.util.Date());
            signature.setStatus(1);
            signature.setUpdateTime(LocalDateTime.now());
            electronicSignatureMapper.updateById(signature);
            log.info("{} 完成合同签署 leaseId={}", userType, id);
        } else {
            log.warn("签章已完成或不存在 leaseId={}, userId={}, userType={}, signatureStatus={}", 
                     id, userId, userType, signature != null ? signature.getStatus() : null);
            return true;
        }

        QueryWrapper<ElectronicSignature> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("agreement_id", id);
        List<ElectronicSignature> allSignatures = electronicSignatureMapper.selectList(checkWrapper);
        
        boolean landlordSigned = false;
        boolean tenantSigned = false;
        
        for (ElectronicSignature s : allSignatures) {
            if (s.getStatus() == 1) {
                if ("LANDLORD".equals(s.getUserType())) {
                    landlordSigned = true;
                } else if ("TENANT".equals(s.getUserType())) {
                    tenantSigned = true;
                }
            }
        }
        
        // 只有房东和租客都签署了，合同才生效
        if (landlordSigned && tenantSigned) {
            lease.setStatus(2); // 状态改为生效中
            lease.setSigningDate(new java.util.Date());
            lease.setEffectiveDate(new java.util.Date());
            lease.setUpdateTime(LocalDateTime.now());
            
            String contractUrl = generateContract(lease);
            if (contractUrl != null) {
                lease.setContractUrl(contractUrl);
            }
            
            baseMapper.updateById(lease);
            log.info("合同双方已完成签署，合同生效 leaseId={}, contractUrl={}", id, contractUrl);
        }

        return true;
    }

    private String generateContract(LeaseAgreement lease) {
        try {
            byte[] content = generateContractBytes(lease);
            if (content == null) {
                return null;
            }
            
            String filename = "contract_" + lease.getLeaseNo() + ".docx";
            
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() { return "file"; }
                @Override
                public String getOriginalFilename() { return filename; }
                @Override
                public String getContentType() { return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; }
                @Override
                public boolean isEmpty() { return content.length == 0; }
                @Override
                public long getSize() { return content.length; }
                @Override
                public byte[] getBytes() { return content; }
                @Override
                public InputStream getInputStream() { return new ByteArrayInputStream(content); }
                @Override
                public void transferTo(java.io.File dest) {
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                        fos.write(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            
            String url = fileService.upload(multipartFile);
            log.info("合同文件上传成功: {}", url);
            return url;
        } catch (Exception e) {
            log.error("生成合同失败, leaseId={}, error={}", lease.getId(), e.getMessage(), e);
            return null;
        }
    }

    private byte[] generateContractBytes(LeaseAgreement lease) {
        try {
            fillLeaseDetails(lease);
            
            Map<String, Object> data = new HashMap<>();
            data.put("leaseNo", lease.getLeaseNo());
            data.put("signingDate", lease.getSigningDate() != null ? 
                java.time.LocalDateTime.ofInstant(lease.getSigningDate().toInstant(), 
                    java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "");
            
            if (lease.getHouse() != null) {
                data.put("houseAddress", lease.getHouse().getAddress());
                data.put("houseType", lease.getHouse().getHouseType());
                data.put("area", lease.getHouse().getArea());
            }
            
            if (lease.getTenant() != null) {
                data.put("tenantName", lease.getTenant().getUsername());
                data.put("tenantPhone", lease.getTenant().getPhone());
                data.put("tenantIdCard", lease.getTenant().getIdCard() != null ? 
                    maskIdCard(lease.getTenant().getIdCard()) : "");
            }
            
            if (lease.getLandlord() != null) {
                data.put("landlordName", lease.getLandlord().getUsername());
                data.put("landlordPhone", lease.getLandlord().getPhone());
                data.put("landlordIdCard", lease.getLandlord().getIdCard() != null ? 
                    maskIdCard(lease.getLandlord().getIdCard()) : "");
            }
            
            if (lease.getStartDate() != null) {
                data.put("startDate", java.time.LocalDateTime.ofInstant(lease.getStartDate().toInstant(), 
                    java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            }
            if (lease.getEndDate() != null) {
                data.put("endDate", java.time.LocalDateTime.ofInstant(lease.getEndDate().toInstant(), 
                    java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            }
            
            data.put("rentPrice", lease.getRentPrice());
            data.put("deposit", lease.getDeposit());
            
            String paymentWayStr = "";
            if (lease.getPaymentWay() != null) {
                switch (lease.getPaymentWay()) {
                    case 1: paymentWayStr = "月付"; break;
                    case 2: paymentWayStr = "季付"; break;
                    case 3: paymentWayStr = "半年付"; break;
                    case 4: paymentWayStr = "年付"; break;
                }
            }
            data.put("paymentWay", paymentWayStr);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                XWPFDocument document = new XWPFDocument();
                
                XWPFParagraph titleParagraph = document.createParagraph();
                titleParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("房屋租赁合同");
                titleRun.setBold(true);
                titleRun.setFontSize(18);
                
                document.createParagraph().createRun().setText("合同编号：" + lease.getLeaseNo());
                
                XWPFParagraph dateParagraph = document.createParagraph();
                dateParagraph.setAlignment(ParagraphAlignment.RIGHT);
                dateParagraph.createRun().setText("签订日期：" + data.get("signingDate"));
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph partyParagraph = document.createParagraph();
                XWPFRun partyRun = partyParagraph.createRun();
                partyRun.setText("甲方（房东）：" + data.get("landlordName"));
                partyParagraph.createRun().setText("\n");
                partyParagraph.createRun().setText("身份证号：" + data.get("landlordIdCard"));
                partyParagraph.createRun().setText("\n");
                partyParagraph.createRun().setText("联系电话：" + data.get("landlordPhone"));
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph tenantParagraph = document.createParagraph();
                tenantParagraph.createRun().setText("乙方（租客）：" + data.get("tenantName"));
                tenantParagraph.createRun().setText("\n");
                tenantParagraph.createRun().setText("身份证号：" + data.get("tenantIdCard"));
                tenantParagraph.createRun().setText("\n");
                tenantParagraph.createRun().setText("联系电话：" + data.get("tenantPhone"));
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph introParagraph = document.createParagraph();
                introParagraph.createRun().setText("根据《中华人民共和国合同法》及相关法律法规，甲乙双方在平等、自愿、公平、诚实信用的基础上，就房屋租赁事宜达成如下协议：");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause1 = document.createParagraph();
                XWPFRun clause1Run = clause1.createRun();
                clause1Run.setBold(true);
                clause1Run.setText("第一条 房屋基本情况");
                clause1.createRun().setText("\n");
                clause1.createRun().setText("房屋坐落于：" + data.get("houseAddress"));
                clause1.createRun().setText("\n");
                clause1.createRun().setText("房屋类型：" + data.get("houseType"));
                clause1.createRun().setText("\n");
                clause1.createRun().setText("建筑面积：" + data.get("area") + "平方米");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause2 = document.createParagraph();
                XWPFRun clause2Run = clause2.createRun();
                clause2Run.setBold(true);
                clause2Run.setText("第二条 租赁期限");
                clause2.createRun().setText("\n");
                clause2.createRun().setText("租赁期限自" + data.get("startDate") + "至" + data.get("endDate") + "。");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause3 = document.createParagraph();
                XWPFRun clause3Run = clause3.createRun();
                clause3Run.setBold(true);
                clause3Run.setText("第三条 租金及支付方式");
                clause3.createRun().setText("\n");
                clause3.createRun().setText("1. 月租金：人民币" + data.get("rentPrice") + "元");
                clause3.createRun().setText("\n");
                clause3.createRun().setText("2. 押金：人民币" + data.get("deposit") + "元");
                clause3.createRun().setText("\n");
                clause3.createRun().setText("3. 支付方式：" + data.get("paymentWay"));
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause4 = document.createParagraph();
                XWPFRun clause4Run = clause4.createRun();
                clause4Run.setBold(true);
                clause4Run.setText("第四条 双方权利义务");
                clause4.createRun().setText("\n");
                clause4.createRun().setText("（内容省略...）");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause5 = document.createParagraph();
                XWPFRun clause5Run = clause5.createRun();
                clause5Run.setBold(true);
                clause5Run.setText("第五条 违约责任");
                clause5.createRun().setText("\n");
                clause5.createRun().setText("（内容省略...）");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph clause6 = document.createParagraph();
                XWPFRun clause6Run = clause6.createRun();
                clause6Run.setBold(true);
                clause6Run.setText("第六条 其他约定");
                clause6.createRun().setText("\n");
                clause6.createRun().setText("（内容省略...）");
                
                document.createParagraph().createRun().setText("\n\n\n");
                
                XWPFParagraph signatureParagraph = document.createParagraph();
                signatureParagraph.setAlignment(ParagraphAlignment.LEFT);
                signatureParagraph.createRun().setText("甲方签字：________________________");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph tenantSignatureParagraph = document.createParagraph();
                tenantSignatureParagraph.setAlignment(ParagraphAlignment.LEFT);
                tenantSignatureParagraph.createRun().setText("乙方签字：________________________");
                
                document.createParagraph().createRun().setText("\n");
                
                XWPFParagraph dateSignParagraph = document.createParagraph();
                dateSignParagraph.setAlignment(ParagraphAlignment.LEFT);
                dateSignParagraph.createRun().setText("日期：" + data.get("signingDate"));
                
                document.write(outputStream);
                document.close();
                
                return outputStream.toByteArray();
            }
            
        } catch (Exception e) {
            log.error("生成合同失败, leaseId={}, error={}", lease.getId(), e.getMessage(), e);
            return null;
        }
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 18) {
            return "";
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(14);
    }

    @Override
    public boolean effectiveLease(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }
        lease.setStatus(2); // 已生效
        lease.setEffectiveDate(new java.util.Date());
        lease.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(lease) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean terminateLease(Long id, String reason) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }

        Long currentUserId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        
        if (!currentUserId.equals(lease.getTenantId()) && !currentUserId.equals(lease.getLandlordId())) {
            throw new com.houserental.common.exception.BusinessException("无权操作他人合同");
        }

        Integer previousStatus = lease.getStatus();
        
        lease.setStatus(4);
        lease.setTerminationDate(new java.util.Date());
        lease.setUpdateTime(LocalDateTime.now());
        
        if (previousStatus != null && previousStatus >= 1 && previousStatus <= 2) {
            if (lease.getDeposit() != null && lease.getDeposit().compareTo(java.math.BigDecimal.ZERO) > 0) {
                walletService.recharge(lease.getTenantId(), lease.getDeposit(), "退租清算-退还押金");
            }
        }

        return baseMapper.updateById(lease) > 0;
    }

    @Override
    public PageResult<LeaseAgreement> pageLeases(Map<String, Object> params) {
        long pageNum = params.containsKey("page") ? Long.parseLong(params.get("page").toString()) : 1;
        long pageSize = params.containsKey("size") ? Long.parseLong(params.get("size").toString()) : 10;
        Page<LeaseAgreement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LeaseAgreement> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (params.containsKey("houseId")) {
            wrapper.eq("house_id", params.get("houseId"));
        }
        if (params.containsKey("tenantId")) {
            wrapper.eq("tenant_id", params.get("tenantId"));
        }
        if (params.containsKey("landlordId")) {
            wrapper.eq("landlord_id", params.get("landlordId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq("status", params.get("status"));
        }

        // 分页查询
        baseMapper.selectPage(page, wrapper);

        // 填充关联数据
        for (LeaseAgreement lease : page.getRecords()) {
            fillLeaseDetails(lease);
        }

        return PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public LeaseAgreement getLeaseById(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease != null) {
            fillLeaseDetails(lease);
        }
        return lease;
    }

    private void fillLeaseDetails(LeaseAgreement lease) {
        // 填充房源信息
        if (lease.getHouseId() != null) {
            House house = houseMapper.selectById(lease.getHouseId());
            lease.setHouse(house);
        }
        // 填充租客信息
        if (lease.getTenantId() != null) {
            User tenant = userMapper.selectById(lease.getTenantId());
            lease.setTenant(tenant);
        }
        // 填充房东信息
        if (lease.getLandlordId() != null) {
            User landlord = userMapper.selectById(lease.getLandlordId());
            lease.setLandlord(landlord);
        }
        // 填充电子签章信息
        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("agreement_id", lease.getId());
        List<ElectronicSignature> signatures = electronicSignatureMapper.selectList(wrapper);
        lease.setSignatures(signatures);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendContract(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }
        
        // 检查是否已存在签名记录
        QueryWrapper<ElectronicSignature> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("agreement_id", id);
        long existingCount = electronicSignatureMapper.selectCount(checkWrapper);
        
        if (existingCount == 0) {
            // 为房东创建签名记录
            ElectronicSignature landlordSignature = new ElectronicSignature();
            landlordSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            landlordSignature.setAgreementId(id);
            landlordSignature.setUserId(lease.getLandlordId());
            landlordSignature.setUserType("LANDLORD");
            landlordSignature.setStatus(0);
            landlordSignature.setCreateTime(LocalDateTime.now());
            landlordSignature.setUpdateTime(LocalDateTime.now());
            electronicSignatureMapper.insert(landlordSignature);
            
            // 为租客创建签名记录
            ElectronicSignature tenantSignature = new ElectronicSignature();
            tenantSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            tenantSignature.setAgreementId(id);
            tenantSignature.setUserId(lease.getTenantId());
            tenantSignature.setUserType("TENANT");
            tenantSignature.setStatus(0);
            tenantSignature.setCreateTime(LocalDateTime.now());
            tenantSignature.setUpdateTime(LocalDateTime.now());
            electronicSignatureMapper.insert(tenantSignature);
            
            log.info("已为合同创建双方签名记录 leaseId={}", id);
        }
        
        lease.setStatus(1);
        lease.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(lease) > 0;
    }

    @Override
    public void exportContract(Long id, javax.servlet.http.HttpServletResponse response) throws IOException {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            response.sendError(404, "合同不存在");
            return;
        }
        
        byte[] contractContent = generateContractBytes(lease);
        if (contractContent == null) {
            response.sendError(500, "合同生成失败");
            return;
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        String fileName = "contract_" + lease.getLeaseNo() + ".docx";
        String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setContentLength(contractContent.length);
        response.setCharacterEncoding("UTF-8");
        
        try (java.io.OutputStream os = response.getOutputStream()) {
            os.write(contractContent);
            os.flush();
        }
        
        String filename = "contract_" + lease.getLeaseNo() + ".docx";
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() { return "file"; }
            @Override
            public String getOriginalFilename() { return filename; }
            @Override
            public String getContentType() { return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; }
            @Override
            public boolean isEmpty() { return contractContent.length == 0; }
            @Override
            public long getSize() { return contractContent.length; }
            @Override
            public byte[] getBytes() { return contractContent; }
            @Override
            public InputStream getInputStream() { return new ByteArrayInputStream(contractContent); }
            @Override
            public void transferTo(java.io.File dest) {
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                    fos.write(contractContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        String url = fileService.upload(multipartFile);
        log.info("合同文件上传成功: {}", url);
        lease.setContractUrl(url);
        baseMapper.updateById(lease);
    }

    @Override
    public boolean generateBill(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
        }
        
        // 检查该合同本月是否已经生成过账单
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        QueryWrapper<com.houserental.entity.Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lease_id", id);
        queryWrapper.apply("YEAR(create_time) = {0}", year);
        queryWrapper.apply("MONTH(create_time) = {0}", month);
        
        long count = billMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new com.houserental.common.exception.BusinessException("该合同本月已生成过账单");
        }
        
        com.houserental.entity.Bill bill = new com.houserental.entity.Bill();
        bill.setLeaseId(id);
        bill.setHouseId(lease.getHouseId());
        bill.setTenantId(lease.getTenantId());
        bill.setLandlordId(lease.getLandlordId());
        bill.setAmount(lease.getRentPrice());
        bill.setStatus(1);
        bill.setBillType(1);
        bill.setCreateTime(LocalDateTime.now());
        
        return billMapper.insert(bill) > 0;
    }
}
