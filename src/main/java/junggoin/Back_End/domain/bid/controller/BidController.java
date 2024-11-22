package junggoin.Back_End.domain.bid.controller;

import junggoin.Back_End.domain.bid.dto.BidRequestDTO;
import junggoin.Back_End.domain.bid.dto.BidResponseDTO;
import junggoin.Back_End.domain.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auctions")
@RequiredArgsConstructor
@RestController
public class BidController {
    private final BidService bidService;

    // 입찰하기
    @PostMapping("{auctionId}/bid")
    public ResponseEntity<BidResponseDTO> bidAuction(@PathVariable Long auctionId, @RequestBody
    BidRequestDTO bidRequestDto) {
        return ResponseEntity.ok(bidService.bidAuction(auctionId,bidRequestDto)) ;
    }
}
