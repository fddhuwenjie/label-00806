package com.fooddelivery.controller.wx;

import com.fooddelivery.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/wx/file")
public class WxFileController {

    @Value("${file.upload-dir:/tmp/uploads}")
    private String uploadDir;

    @Value("${file.access-url:http://localhost:8080/uploads}")
    private String accessUrl;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            Path dirPath = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(dirPath);

            Path destPath = dirPath.resolve(fileName);
            file.transferTo(destPath.toFile());

            String url = accessUrl + "/" + fileName;
            log.info("文件上传成功: {}, 保存路径: {}", url, destPath);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
