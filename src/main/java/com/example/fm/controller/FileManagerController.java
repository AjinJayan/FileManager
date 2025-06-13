package com.example.fm.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fm.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Slf4j
public class FileManagerController {
    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload-file")
    // file are MultipartFile
    // One thing we need to mention is the what is the maximun size of the multipart
    // file
    public boolean uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileStorageService.saveFile(file);
            return true;
        } catch (IOException e) {
            log.error("Excepton During Upload", e);
        }
        return false;
    }

    @GetMapping("/download")
    // We will be sending a Java.core.io.Resource as a response
    public ResponseEntity<Resource> getMethodName(@RequestParam("file") String filename) throws IOException {
        try {
            File fileToDownload = fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // add this
                                                                                                          // will save
                                                                                                          // file with
                                                                                                          // filename
                                                                                                          // Content-Disposition:
                                                                                                          // attachment;
                                                                                                          // filename="report.pdf"
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download-faster")
    // This menthod will download files in chunks in parallel, also provide resume
    // funtionality, (These features lack in the previous method)
    // This the faster download
    public ResponseEntity<Resource> downloadFaster(@RequestParam("file") String filename) {
        try {
            File fileToDownload = fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // add this
                                                                                                          // will save
                                                                                                          // file with
                                                                                                          // filename
                                                                                                          // Content-Disposition:
                                                                                                          // attachment;
                                                                                                          // filename="report.pdf"
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
