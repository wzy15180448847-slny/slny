package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
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
import org.springframework.core.io.ClassPathResource;
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

        if (house.getHouseStatus() != null && house.getHouseStatus() == 1) {
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
        tenantSignature.setLeaseId(lease.getId());
        tenantSignature.setUserId(lease.getTenantId());
        tenantSignature.setUserType("TENANT");
        tenantSignature.setStatus(0); // 待签章
        tenantSignature.setCreateTime(LocalDateTime.now());
        tenantSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(tenantSignature);

        // 创建房东签章记录
        ElectronicSignature landlordSignature = new ElectronicSignature();
        landlordSignature.setSignatureNo("SIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        landlordSignature.setLeaseId(lease.getId());
        landlordSignature.setUserId(lease.getLandlordId());
        landlordSignature.setUserType("LANDLORD");
        landlordSignature.setStatus(0); // 待签章
        landlordSignature.setCreateTime(LocalDateTime.now());
        landlordSignature.setUpdateTime(LocalDateTime.now());
        electronicSignatureMapper.insert(landlordSignature);
    }

    @Override
    @Transactional
    public boolean signLease(Long id, Long userId, String userType, String signatureData) {
        QueryWrapper<LeaseAgreement> lockWrapper = new QueryWrapper<>();
        lockWrapper.eq("id", id).last("FOR UPDATE");
        LeaseAgreement lease = baseMapper.selectOne(lockWrapper);
        if (lease == null) {
            return false;
        }

        QueryWrapper<ElectronicSignature> wrapper = new QueryWrapper<>();
        wrapper.eq("lease_id", id);
        wrapper.eq("user_id", userId);
        wrapper.eq("user_type", userType);
        ElectronicSignature signature = electronicSignatureMapper.selectOne(wrapper);
        if (signature != null && signature.getStatus() == 0) {
            signature.setSignatureData(signatureData);
            signature.setSigningTime(new java.util.Date());
            signature.setStatus(1);
            signature.setUpdateTime(LocalDateTime.now());
            electronicSignatureMapper.updateById(signature);
        } else {
            log.warn("签章已完成或不存在, leaseId={}, userId={}, userType={}", id, userId, userType);
            return true;
        }

        QueryWrapper<ElectronicSignature> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("lease_id", id);
        checkWrapper.eq("status", 0);
        long count = electronicSignatureMapper.selectCount(checkWrapper);
        if (count == 0) {
            lease.setStatus(1);
            lease.setSigningDate(new java.util.Date());
            lease.setUpdateTime(LocalDateTime.now());
            
            String contractUrl = generateContract(lease);
            if (contractUrl != null) {
                lease.setContractUrl(contractUrl);
            }
            
            baseMapper.updateById(lease);
            log.info("租约签署完成并生成合同, leaseId={}, contractUrl={}", id, contractUrl);
        }

        return true;
    }

    private String generateContract(LeaseAgreement lease) {
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

            ClassPathResource resource = new ClassPathResource("templates/contract_template.docx");
            if (!resource.exists()) {
                log.warn("合同模板文件不存在");
                return null;
            }
            
            try (InputStream inputStream = resource.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                XWPFTemplate template = XWPFTemplate.compile(inputStream).render(data);
                template.write(outputStream);
                template.close();
                
                byte[] content = outputStream.toByteArray();
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
        Page<LeaseAgreement> page = new Page<>(Long.parseLong(params.get("page").toString()), Long.parseLong(params.get("size").toString()));
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
        wrapper.eq("lease_id", lease.getId());
        List<ElectronicSignature> signatures = electronicSignatureMapper.selectList(wrapper);
        lease.setSignatures(signatures);
    }

    @Override
    public boolean sendContract(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
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
        
        String fileUrl = generateContract(lease);
        if (fileUrl == null) {
            response.sendError(500, "合同生成失败");
            return;
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=\"contract_" + lease.getLeaseNo() + ".docx\"");
        
        java.net.URL url = new java.net.URL(fileUrl);
        try (java.io.InputStream is = url.openStream();
             java.io.OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public boolean generateBill(Long id) {
        LeaseAgreement lease = baseMapper.selectById(id);
        if (lease == null) {
            return false;
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