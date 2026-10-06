package core.application.reviews.services.images;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageUploadService implements ImageUploadService {

    /**
     * S3 Client
     */
    private final S3Client s3Client;

    /**
     * S3 버킷 이름
     */
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    /**
     * S3 버킷 내 이미지 저장 경로
     */
    @Value("${aws.s3.bucket.upload-folder}")
    private String imageFolder;

    /**
     * AWS Region
     */
    @Value("${aws.region}")
    private String region;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {

        log.info("Uploading image to S3");
        logFileInfo(file);

        String objectKey =
                imageFolder + "/" + UUID.randomUUID();

        String contentType = file.getContentType();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        log.info("Image uploaded to S3 successfully");
        log.info("bucket={}, key={}", bucketName, objectKey);

        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                objectKey
        );
    }

    private void logFileInfo(MultipartFile file) {
        log.info("File info : {}", file);
        log.info("Name : {}", file.getName());
        log.info("ContentType : {}", file.getContentType());

        long sizeBytes = file.getSize();

        if (sizeBytes <= 0) {
            log.info("Size : 0 B");
            return;
        }

        int logarithm = Math.min(
                ((int) Math.log10(sizeBytes)) / 3,
                6
        );

        char unit = " KMGTPE".charAt(logarithm);
        double size = sizeBytes / Math.pow(1_000L, logarithm);

        log.info("Size : {}", String.format("%.2f %cB", size, unit));
    }
}