package com.houserental.service.impl;

import com.houserental.config.MinioConfig;
import com.houserental.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务实现类
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    public void init() {
        try {
            // 初始化时检查 MinIO 连接并创建桶
            checkAndCreateBucket();
            log.info("MinIO 连接成功，存储桶 '{}' 已就绪", minioConfig.getBucketName());
        } catch (Exception e) {
            log.warn("MinIO 初始化失败，请检查 MinIO 是否启动：{}", e.getMessage());
            log.warn("文件上传功能将暂时不可用，直到 MinIO 连接恢复");
        }
    }

    private void checkAndCreateBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioConfig.getBucketName())
                .build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build());
            log.info("创建存储桶：{}", minioConfig.getBucketName());
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            // 确保桶存在
            checkAndCreateBucket();

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";
            String fileName = "house/" + UUID.randomUUID() + fileExtension;

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            
            // 上传成功后设置 bucket 为公开读取
            try {
                setBucketPublic();
                log.info("Bucket 已设置为公开读取");
            } catch (Exception policyEx) {
                log.warn("设置 Bucket 公开失败：{}", policyEx.getMessage());
            }

            log.info("文件上传成功：{}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage() + "，请检查 MinIO 服务是否正常运行", e);
        }
    }

    public void setBucketPublic() throws Exception {
        // 标准的 MinIO 公开读取 Policy JSON
        // 允许任何用户 (*) 对 bucket 中的所有对象执行 s3:GetObject 操作
        String policyJson = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": \"*\",\n" +
                "            \"Action\": [\"s3:GetObject\"],\n" +
                "            \"Resource\": [\"arn:aws:s3:::" + minioConfig.getBucketName() + "/*\"]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(minioConfig.getBucketName())
                .config(policyJson)
                .build());
        
        log.info("存储桶 '{}' 已设置为公开读取 (Public Read-Only)", minioConfig.getBucketName());
    }

    @Override
    public List<String> uploadBatch(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            fileNames.add(upload(file));
        }
        return fileNames;
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
            log.info("文件删除成功: {}", fileName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        try {
            // 生成预签名URL，有效期7天
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .method(Method.GET)
                    .expiry(7, TimeUnit.DAYS)
                    .build());
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new RuntimeException("获取文件URL失败", e);
        }
    }
}
