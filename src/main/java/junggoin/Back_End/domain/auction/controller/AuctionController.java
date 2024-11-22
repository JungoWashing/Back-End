package junggoin.Back_End.domain.auction.controller;

import java.util.List;
import java.util.Map;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import junggoin.Back_End.domain.auction.dto.ProductReqDto;
import junggoin.Back_End.domain.auction.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("/create")
    public ResponseEntity<ProductRepDto> createAuction(
            @PathVariable("email") String email,
            @RequestBody ProductReqDto productReqDto
    ) {
        return ResponseEntity.ok(auctionService.createAuction(email, productReqDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRepDto> getAuctionById(@PathVariable("id") Long id) {
        Auction auction = auctionService.findById(id);
        return ResponseEntity.ok(ProductRepDto.builder()
                .productName(auction.getItemName())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .endTime(auction.getExpiredAt())
                .status(auction.getStatus())
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.findAll());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteAuction(@PathVariable("id") Long id) {
        auctionService.deleteAuction(id);
        return ResponseEntity.ok("경매 삭제 성공");
    }
}
