package junggoin.Back_End.domain.bid.service;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.Status;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.bid.Bid;
import junggoin.Back_End.domain.bid.dto.BidRequestDTO;
import junggoin.Back_End.domain.bid.dto.BidResponseDTO;
import junggoin.Back_End.domain.bid.repository.BidRepository;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final MemberService memberService;
    private final RedisLockRegistry redisLockRegistry;

    @Transactional
    public BidResponseDTO startBid(int price, Member bidder,Long auctionId) {
        // 경매 상태 확인
        Auction auction = auctionService.findById(auctionId);
        if(auction.getStatus()== Status.CLOSED ) {
            throw new RuntimeException("종료된 경매");
        }

        // 입찰가 검증

        int startingPrice = auction.getStartingPrice();
        if(price< auction.getStartingPrice()){
            throw new IllegalArgumentException("시작 입찰가부터 입찰 가능합니다 " + startingPrice);
        }

        int currentWinningPrice = auction.getWinningPrice();
        if (price <= currentWinningPrice) {
            throw new IllegalArgumentException("현재 최고 입찰가 보다 커야합니다 " + currentWinningPrice);
        }

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(bidder)
                .bidPrice(price)
                .build();

        bidRepository.save(bid);

        auction.updateWinningPrice(price);


        return BidResponseDTO.builder()
                .bidId(bid.getId())
                .bidderId(bidder.getId())
                .price(price)
                .auctionId(auctionId)
                .build();
    }

    @Synchronized
    @Transactional
    public BidResponseDTO bidAuction(Long auctionId,BidRequestDTO bidRequestDto) {
        log.info("{}", bidRequestDto.getEmail());
        Member bidder = memberService.findMemberByEmail(bidRequestDto.getEmail()).orElseThrow(()-> new NoSuchElementException("존재하지 않는 회원"));
        String lockKey = String.format("ProductId:%d", auctionId);

        try {
            return redisLockRegistry.executeLocked(lockKey, Duration.ofSeconds(5) ,() -> startBid(bidRequestDto.getPrice(), bidder, auctionId));
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 낙찰자 찾기
    @Transactional
    public Long getWinnerId(Long auctionId){
            Bid bid = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId).orElseThrow(()->new NoSuchElementException("존재하지 않는 입찰"));
        return bid.getBidder().getId();
    }

    // 해당 경매 입찰 리스트
    @Transactional
    public List<Bid> getBids(Long auctionId) {
        return bidRepository.findAllByAuctionId(auctionId);
    }
}
