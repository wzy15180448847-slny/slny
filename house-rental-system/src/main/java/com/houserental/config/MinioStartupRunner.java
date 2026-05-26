package com.houserental.config;

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
            io.minio.MinioClient client = io.minio.MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

            String policyJson = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::house-rental/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            client.setBucketPolicy(io.minio.SetBucketPolicyArgs.builder()
                    .bucket("house-rental")
                    .config(policyJson)
                    .build());

            log.info("✅ Bucket 'house-rental' 已设置为公开读取");
        } catch (Exception e) {
            log.error("设置 Bucket 公开失败：{}", e.getMessage());
        }
    }
}
