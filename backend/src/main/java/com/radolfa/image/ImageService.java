package com.radolfa.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * End-to-end image upload pipeline.
 * <ol>
 *   <li><b>Validate</b> — content-type whitelist, size &le; 5 MB.</li>
 *   <li><b>Resize / Compress</b> — Thumbnailator caps longest side at 1920 px,
 *       outputs WebP at 80 % quality.</li>
 *   <li><b>Upload</b> — PutObject to the configured S3 bucket with public-read ACL.</li>
 *   <li><b>Return</b> — the public HTTPS URL of the uploaded object.</li>
 * </ol>
 */
@Service
@Slf4j
public class ImageService {

    private static final long       MAX_SIZE_BYTES = 5L * 1024 * 1024;   // 5 MB
    private static final int        MAX_DIMENSION  = 1920;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/webp"
    );

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    public ImageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Runs the full pipeline and returns the public S3 URL of the processed image.
     */
    public String uploadImage(MultipartFile file) {
        validate(file);
        try {
            byte[] webp = resizeAndCompress(file);
            String key  = "products/" + UUID.randomUUID() + ".webp";
            uploadToS3(key, webp);
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
        } catch (IllegalArgumentException e) {
            throw e;   // validation errors propagate as-is
        } catch (Exception e) {
            log.error("ImageService: pipeline failure.", e);
            throw new RuntimeException("Image processing failed.", e);
        }
    }

    // --------------------------------------------------------
    // Step A – Validation
    // --------------------------------------------------------
    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported image type: " + file.getContentType());
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File exceeds the 5 MB limit.");
        }
    }

    // --------------------------------------------------------
    // Step B – Resize & Compress via Thumbnailator
    //   Output: WebP, 80 % quality, longest side <= 1920 px
    // --------------------------------------------------------
    private byte[] resizeAndCompress(MultipartFile file) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .keepAspectRatio(true)
                .outputFormat("webp")
                .outputQuality(0.80)
                .toOutputStream(out);
        return out.toByteArray();
    }

    // --------------------------------------------------------
    // Step C – Upload to S3 (public-read)
    // --------------------------------------------------------
    private void uploadToS3(String key, byte[] data) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType("image/webp")
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(data));
        log.info("ImageService: uploaded s3://{}/{}", bucketName, key);
    }
}
