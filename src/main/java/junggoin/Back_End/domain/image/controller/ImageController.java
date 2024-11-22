package junggoin.Back_End.domain.image.controller;
import junggoin.Back_End.domain.image.dto.ImageUploadResponseDTO;
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

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponseDTO> uploadImage(@RequestParam("files") List<MultipartFile> files, @RequestParam("auctionId") Long auctionId) {
        return ResponseEntity.ok(imageService.upload(files, auctionId));
    }

//    @GetMapping("/all/{auctionId}")
//    public ResponseEntity<List<Map<String, String>>> getAllImagesByAuction(@PathVariable("auctionId") Long auctionId) {
//        return imageService.getAllImagesByAuction(auctionId);
//    }
}
