package com.houserental.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传单个文件
     */
    String upload(MultipartFile file);

    /**
     * 批量上传文件
     */
    List<String> uploadBatch(List<MultipartFile> files);

    /**
     * 删除文件
     */
    void delete(String fileName);

    /**
     * 获取文件访问URL
     */
    String getFileUrl(String fileName);
}
