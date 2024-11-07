package junggoin.Back_End.domain.image.service;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.repository.AuctionRepository;
import junggoin.Back_End.domain.image.Image;
import junggoin.Back_End.domain.image.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    public ResponseEntity<Map<String, Object>> uploadImage(MultipartFile file, Long auctionId) {
        try {
            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

            Image image = Image.builder()
                    .data(file.getBytes())
                    .auction(auction)
                    .build();

            Image savedImage = imageRepository.save(image);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedImage.getId());
            response.put("message", "Image uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("message", "Image upload failed"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Map<String, String>>> getAllImagesByAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

        List<Map<String, String>> images = auction.getImages().stream().map(image -> {
            Map<String, String> imgData = new HashMap<>();
            imgData.put("id", String.valueOf(image.getId()));
            imgData.put("data", Base64.getEncoder().encodeToString(image.getData()));
            return imgData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(images);
    }
}
