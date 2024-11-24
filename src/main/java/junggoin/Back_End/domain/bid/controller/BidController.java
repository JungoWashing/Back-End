package junggoin.Back_End.domain.bid.controller;

import junggoin.Back_End.domain.bid.dto.BidRequestDto;
import junggoin.Back_End.domain.bid.dto.BidResponseDto;
import junggoin.Back_End.domain.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RequestMapping("api/auctions")
@RequiredArgsConstructor
@RestController
public class BidController {
    private final BidService bidService;

    // 입찰하기
    @PostMapping("{auctionId}/bid")
    public ResponseEntity<BidResponseDto> bidAuction(@PathVariable Long auctionId, @RequestBody
    BidRequestDto bidRequestDto) throws InterruptedException, TimeoutException {
        return ResponseEntity.ok(bidService.bidAuction(auctionId,bidRequestDto)) ;
    }
}
