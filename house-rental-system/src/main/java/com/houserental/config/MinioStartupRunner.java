package com.houserental.config;

import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MinioStartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MinioStartupRunner.class);

    @Value("${app.minio.path:D:/MinIO}")
    private String minioPath;

    @Value("${app.minio.data-path:D:/MinIO/data}")
    private String minioDataPath;

    @Value("${app.minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${app.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${app.minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${app.minio.bucket-name:house-rental}")
    private String bucketName;

    private Process minioProcess;

    @Override
    public void run(String... args) throws Exception {
        try {
            File minioExecutable = new File(minioPath, "minio.exe");
            if (!minioExecutable.exists()) {
                log.warn("MinIO executable not found at: {}", minioExecutable.getAbsolutePath());
                log.warn("File upload functionality may not work properly");
                return;
            }

            File dataDir = new File(minioDataPath);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                log.info("Created MinIO data directory: {}", dataDir.getAbsolutePath());
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    minioExecutable.getAbsolutePath(),
                    "server",
                    dataDir.getAbsolutePath(),
                    "--console-address", ":9001"
            );
            processBuilder.redirectErrorStream(true);
            processBuilder.inheritIO();

            log.info("Starting MinIO server...");
            minioProcess = processBuilder.start();
            log.info("MinIO server started successfully");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (minioProcess != null && minioProcess.isAlive()) {
                    log.info("Stopping MinIO server...");
                    minioProcess.destroy();
                }
            }));

            // 等待 MinIO 完全启动
            Thread.sleep(5000);
            
            // 设置 bucket 为公开读取
            setBucketPolicy();

        } catch (Exception e) {
            log.error("Failed to start MinIO server", e);
            log.warn("File upload functionality may not work properly");
        }
    }

    private void setBucketPolicy() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            // 标准的 MinIO 公开读取 Policy JSON
            // 允许任何用户 (*) 对 bucket 中的所有对象执行 s3:GetObject 操作
            String policyJson = "{\n" +
                    "    \"Version\": \"2012-10-17\",\n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Effect\": \"Allow\",\n" +
                    "            \"Principal\": \"*\",\n" +
                    "            \"Action\": [\"s3:GetObject\"],\n" +
                    "            \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policyJson)
                    .build());

            log.info("✅ Bucket '{}' 已设置为公开读取 (Public Read-Only)", bucketName);
            log.info("📝 Policy JSON: {}", policyJson);
        } catch (Exception e) {
            log.error("❌ 设置 Bucket 公开读取失败：{}", e.getMessage(), e);
            log.warn("⚠️  前端可能无法直接访问上传的图片，需要手动在 MinIO 控制台设置 Bucket Policy");
        }
    }
}
