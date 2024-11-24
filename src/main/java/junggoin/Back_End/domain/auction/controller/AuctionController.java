package junggoin.Back_End.domain.auction.controller;

import java.util.List;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import junggoin.Back_End.domain.auction.dto.ProductReqDto;
import junggoin.Back_End.domain.auction.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 게시
    @PostMapping("/create/{email}")
    public ResponseEntity<ProductRepDto> createAuction(
            @PathVariable("email") String email,
            @RequestBody ProductReqDto productReqDto
    ) {
        return ResponseEntity.ok(auctionService.createAuction(email, productReqDto));
    }

    // 경매 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductRepDto> getAuctionById(@PathVariable("id") Long id) {
        Auction auction = auctionService.findById(id);
        return ResponseEntity.ok(auctionService.toProductRepDto(auction));
    }

    // 경매 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<ProductRepDto>> getAllAuctions() {
        List<ProductRepDto> productRepDtos = auctionService.findAll().stream().map(auctionService::toProductRepDto).toList();
        return ResponseEntity.ok(productRepDtos);
    }

    // 경매 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable("id") Long id) {
        Long auctionId = auctionService.deleteAuction(id);

        return ResponseEntity.ok("경매 삭제 성공: "+auctionId);
    }
}
