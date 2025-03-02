package com.example.task_manager.controllers;

import com.example.task_manager.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final StorageService storageService;


    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file){
        try{
            String fileUrl = storageService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e){
            return ResponseEntity.internalServerError().body("File upload failed");
        }
    }
}
