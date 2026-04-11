package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String fileName = fileService.upload(file);
        return Result.success(fileName);
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    public Result<List<String>> uploadBatch(@RequestParam("files") List<MultipartFile> files) {
        List<String> fileNames = fileService.uploadBatch(files);
        return Result.success(fileNames);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("fileName") String fileName) {
        fileService.delete(fileName);
        return Result.success();
    }

    /**
     * 获取文件访问URL
     */
    @GetMapping("/url")
    public Result<String> getFileUrl(@RequestParam("fileName") String fileName) {
        String url = fileService.getFileUrl(fileName);
        return Result.success(url);
    }
}
