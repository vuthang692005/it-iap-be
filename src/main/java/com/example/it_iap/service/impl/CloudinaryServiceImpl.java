package com.example.it_iap.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.CloudinaryService;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private static final String[] ALLOWED_EXTENSIONS = { "jpg", "png", "gif" };
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /*
     * Hàm upload avatar user
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            validateFile(file);

            // XÁC ĐỊNH RESOURCE TYPE
            String resourceType = "image";

            // Upload lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "folder", folder, // Tên thư mục lưu
                            "overwrite", false, // Ghi đè nếu trùng file name
                            "unique_filename", true)); // tên file là duy nhất

            return (String) uploadResult.get("secure_url"); // trả về url file lưu trên cloud

        } catch (IOException e) {
            log.error("Error uploading file: ", e);
            throw new AppException(ErrorCode.UNABLE_TO_UPLOAD_IMAGE);
        }
    }

    /*
     * Hàm hỗ trợ
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.INVALID_IMAGE_SIZE);
        }

        String fileName = file.getOriginalFilename();
        String extension = getFileExtension(fileName);

        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
        }

    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
