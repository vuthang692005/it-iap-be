package com.example.it_iap.service;

import com.example.it_iap.enums.UploadFolder;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, UploadFolder uploadFolder);
}
