package junggoin.Back_End.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import junggoin.Back_End.domain.ai.service.AiService;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.image.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageService {

    private final AmazonS3 amazonS3;
    private final AuctionService auctionService;
    private final AiService aiService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public ImageResponseDto upload(List<MultipartFile> images, Long auctionId) {
        // 이미지 확인
        if (images.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 적어도 1개 이상 올려야합니다.");
        }

        // 경매 먼저 확인
        Auction auction = auctionService.findById(auctionId);

        // 이미지 업로드
        List<String> urls = new ArrayList<>();
        images.forEach(image -> {
            String filename = "images/auctions/" + auctionId + "/" + images.indexOf(image);
            String url = uploadImage(image, filename);
            urls.add(url);
        });

        auction.updateImageUrls(urls);

        aiService.analyzeProduct(auctionId);

        return ImageResponseDto.builder()
                .imageUrls(urls)
                .auctionId(auctionId)
                .build();
    }

    private String uploadImage(MultipartFile image, String filename) {
        this.validateImageFileExtention(Objects.requireNonNull(image.getOriginalFilename()));
        try {
            return this.uploadImageToS3(image, filename);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    //
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new RuntimeException("파일 확장자가 있어야합니다.");
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new RuntimeException("허용되지 않은 파일 형식입니다. 업로드 가능한 확장자는 jpg, jpeg, png, gif입니다.");
        }
    }

    private String uploadImageToS3(MultipartFile image, String filename) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")); //확장자 명
        filename = filename + extension;

        // 이미지 읽기
        InputStream is = image.getInputStream();
        BufferedImage originalImage = ImageIO.read(is);

        // 이미지 리사이징
        BufferedImage resizedImage = resizeImage(originalImage, 900,900);

        // 리사이징된 이미지를 ByteArrayOutputStream에 쓰기
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extension.replace(".", ""), baos);
        byte[] bytes = baos.toByteArray();

        log.info("{} resized: {}", originalFilename, baos.size());

        // S3에 업로드하기 위한 Metadata 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension.replace(".", ""));
        metadata.setContentLength(bytes.length);

        // S3에 업로드
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, filename, byteArrayInputStream, metadata);
        amazonS3.putObject(putObjectRequest);

        // 스트림 닫기
        byteArrayInputStream.close();
        is.close();
        baos.close();

        return amazonS3.getUrl(bucketName, filename).toString();
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws ImagingOpException, IOException {
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    public void deleteImageFromS3(String imageAddress) throws IOException {
        String key = getKeyFromImageAddress(imageAddress);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    // s3는 디렉토리가 따로 없고 key로 구분
    private String getKeyFromImageAddress(String imageAddress) throws MalformedURLException {
        URL url = new URL(imageAddress);
        String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
        return decodingKey.substring(1); // 맨 앞의 '/' 제거
    }

    public ImageResponseDto getImagesByAuction(Long auctionId) {

        return ImageResponseDto.builder()
                .auctionId(auctionId)
                .imageUrls(auctionService.findById(auctionId).getImageUrls())
                .build();
    }
}