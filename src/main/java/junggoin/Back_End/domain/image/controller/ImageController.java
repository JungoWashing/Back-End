package junggoin.Back_End.domain.image.controller;

import junggoin.Back_End.domain.image.dto.ImageResponseDto;
import junggoin.Back_End.domain.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;


    // 경매 상품 이미지 업로드
    @PostMapping("/{auctionId}")
    public ResponseEntity<ImageResponseDto> uploadImage(@RequestParam("files") List<MultipartFile> files, @PathVariable("auctionId") Long auctionId) {
        // 이미지 s3 업로드
        ImageResponseDto imageResponseDto = imageService.upload(files, auctionId);

        // 이미지 분석 요청해서 태그 받기
        //AnalyzeProductResponseDto analyzeProductResponseDto = imageService.analyzeImage(auctionId);
        
        return ResponseEntity.ok(imageResponseDto);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ImageResponseDto> getAllImagesByAuction(@PathVariable("auctionId") Long auctionId) {
        return ResponseEntity.ok(imageService.getImagesByAuction(auctionId));
    }
}
