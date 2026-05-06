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

            Thread.sleep(3000);

        } catch (Exception e) {
            log.error("Failed to start MinIO server", e);
            log.warn("File upload functionality may not work properly");
        }
    }
}
