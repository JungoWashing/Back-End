package junggoin.Back_End.domain.auction.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import junggoin.Back_End.domain.auction.dto.ProductReqDto;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.bid.dto.BidRequestDto;
import junggoin.Back_End.domain.bid.dto.BidResponseDto;
import junggoin.Back_End.domain.bid.service.BidService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;

    // 경매 게시
    @PostMapping("/{email}")
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
    @GetMapping("")
    public ResponseEntity<List<ProductRepDto>> getAllAuctions() {
        List<ProductRepDto> productRepDtos = auctionService.findAll().stream().map(auctionService::toProductRepDto).toList();
        return ResponseEntity.ok(productRepDtos);
    }

    // 경매 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable("id") Long id) {
        Long auctionId = auctionService.deleteAuction(id);

        return ResponseEntity.ok("경매 삭제 성공: "+auctionId);
    }

    // 경매 입찰
    @PostMapping("/{id}/bids")
    public ResponseEntity<BidResponseDto> bidAuction(@PathVariable("id") Long id, @RequestBody
    BidRequestDto bidRequestDto) throws InterruptedException, TimeoutException {
        return ResponseEntity.ok(bidService.bidAuction(id,bidRequestDto)) ;
    }

    // 판매 내역 조회
    @GetMapping("/{email}/sell-history")
    public ResponseEntity<List<ProductRepDto>> getAuctionBySeller(@PathVariable("email") String email) {
        return ResponseEntity.ok(auctionService.findByEmail(email).stream().map(auctionService::toProductRepDto).toList());
    }

    // 구매 내역 조회
    @GetMapping("/{email}/bid-history")
    public ResponseEntity<List<ProductRepDto>> getAuctionByBidder(@PathVariable("email") String email) {
        return ResponseEntity.ok(bidService.findAuctionByEmail(email).stream().map(auctionService::toProductRepDto).toList());
    }
}
