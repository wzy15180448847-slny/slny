# 使用官方Java 11镜像作为基础镜像
FROM openjdk:11-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven构建的jar文件到容器中
COPY house-rental-system/target/house-rental-system-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8081

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]
