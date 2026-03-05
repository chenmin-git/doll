package com.doll.controller;

import com.doll.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(dir.getAbsolutePath() + File.separator + fileName);
            file.transferTo(dest);

            String url = "/api/upload/image/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/image/{filename}")
    public void getImage(@PathVariable String filename,
            javax.servlet.http.HttpServletResponse response) throws IOException {
        File file = new File(uploadDir + File.separator + filename);
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        String contentType = "image/jpeg";
        if ("png".equals(ext))
            contentType = "image/png";
        else if ("gif".equals(ext))
            contentType = "image/gif";
        else if ("webp".equals(ext))
            contentType = "image/webp";

        response.setContentType(contentType);
        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
    }
}
