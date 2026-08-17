package com.example.project.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {
    private final RestClient restClient;
    private final String url;
    private final String bucket;
    private final String serviceKey;

    public SupabaseStorageService(@Value("${supabase.url}") String url,
            @Value("${supabase.service-key}") String serviceKey,
            @Value("${supabase.bucket}") String bucket) {
        this.url = url;
        this.bucket = bucket;
        this.serviceKey = serviceKey;
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public String upload(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn file tài liệu");
        }

        if ("not-configured".equals(serviceKey) || url.contains("example.supabase.co")) {
            throw new RuntimeException("Chưa cấu hình Supabase Storage");
        }

        try {
            String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();

            RestClient.RequestBodySpec request = restClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, path)
                    .header("apikey", serviceKey)
                    .header("x-upsert", "false")
                    .contentType(MediaType.parseMediaType(contentType));

            if (!serviceKey.startsWith("sb_secret_")) {
                request.header("Authorization", "Bearer " + serviceKey);
            }

            request.body(file.getBytes()).retrieve().toBodilessEntity();

            return url + "/storage/v1/object/public/" + bucket + "/" + path;
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file tài liệu");
        } catch (Exception e) {
            throw new RuntimeException("Không thể upload file lên Supabase Storage");
        }
    }

    public void delete(String path) {
        if (path == null || path.isBlank()) return;

        try {
            RestClient.RequestBodySpec request = restClient.method(HttpMethod.DELETE)
                    .uri("/storage/v1/object/{bucket}", bucket)
                    .header("apikey", serviceKey)
                    .contentType(MediaType.APPLICATION_JSON);

            if (!serviceKey.startsWith("sb_secret_")) {
                request.header("Authorization", "Bearer " + serviceKey);
            }

            request.body("[\"" + path.replace("\"", "\\\"") + "\"]").retrieve().toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa file trên Supabase Storage");
        }
    }

    public String getStoragePath(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return null;
        String marker = "/storage/v1/object/public/" + bucket + "/";
        int markerIndex = publicUrl.indexOf(marker);
        if (markerIndex < 0) return null;
        return publicUrl.substring(markerIndex + marker.length());
    }
}
