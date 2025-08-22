package com.cloudnest.objectservice.cloudnestobjectservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/objects")
public class ObjectController {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    public ObjectController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(file.getOriginalFilename()).build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        return "File uploaded: " + file.getOriginalFilename();
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(filename).build(),
                software.amazon.awssdk.core.sync.ResponseTransformer.toBytes()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(objectBytes.asByteArray());
    }

    @GetMapping("/list")
    public List<String> listFiles() {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build())
                .contents()
                .stream()
                .map(S3Object::key)
                .toList();
    }
}
