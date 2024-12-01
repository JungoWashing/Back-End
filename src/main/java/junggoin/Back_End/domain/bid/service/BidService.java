package junggoin.Back_End.domain.bid.service;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.AuctionView;
import junggoin.Back_End.domain.auction.Status;
import junggoin.Back_End.domain.auction.exception.BidNotAllowedException;
import junggoin.Back_End.domain.auction.exception.ClosedAuctionException;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.bid.Bid;
import junggoin.Back_End.domain.bid.dto.BidRequestDto;
import junggoin.Back_End.domain.bid.dto.BidResponseDto;
import junggoin.Back_End.domain.bid.repository.BidRepository;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final MemberService memberService;
    private final RedisLockRegistry redisLockRegistry;

    // 경매 입찰
    public BidResponseDto startBid(int price, Member bidder, Long auctionId) {
        // 경매 상태 확인
        Auction auction = auctionService.findById(auctionId);
        if(auction.getStatus()== Status.CLOSED ) {
            throw new ClosedAuctionException("종료된 경매");
        }

        // 본인 확인
        if(auction.getMember().equals(bidder)) {
            throw new BidNotAllowedException("본인의 경매에는 입찰할 수 없습니다");
        }

        // 입찰가 검증
        int startingPrice = auction.getStartingPrice();
        if(price< auction.getStartingPrice()){
            throw new IllegalArgumentException("시작 입찰가부터 입찰 가능합니다: " + startingPrice);
        }

        int currentWinningPrice = auction.getWinningPrice();
        if (price <= currentWinningPrice) {
            throw new IllegalArgumentException("현재 최고 입찰가 보다 커야합니다: " + currentWinningPrice);
        }

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(bidder)
                .bidPrice(price)
                .build();

        bidRepository.save(bid);
        auctionService.updateWinningPrice(auction,price);

        return this.toBidResponseDto(bid);
    }

    // 경매 입찰 락
    public BidResponseDto bidAuction(Long id, BidRequestDto bidRequestDto) throws InterruptedException, TimeoutException {
        log.info("{}", bidRequestDto.getBidderEmail());
        Member bidder = memberService.findMemberByEmail(bidRequestDto.getBidderEmail()).orElseThrow(()-> new NoSuchElementException("존재하지 않는 회원: "+bidRequestDto.getBidderEmail()));
        String lockKey = String.format("ProductId:%d", id);

        return redisLockRegistry.executeLocked(lockKey, Duration.ofSeconds(5) ,() -> startBid(bidRequestDto.getPrice(), bidder, id));
    }

    // 낙찰자 찾기 (채팅방 생성에 필요)
    public String getWinnerEmail(Long auctionId){
            Bid bid = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId).orElseThrow(()->new NoSuchElementException("존재하지 않는 입찰"));
        return bid.getBidder().getEmail();
    }

    // 입찰한 경매 찾기 (구매/입찰 내역 조회에 필요)
    public List<Auction> findAuctionByEmail(String email){
        List<AuctionView> auctionViews= bidRepository.findDistinctAuctionByBidderEmail(email);
        return auctionViews.stream().map(AuctionView::getAuction).collect(Collectors.toList());
    }

    // 해당 경매 입찰 리스트
    public List<Bid> getBids(Long auctionId) {
        return bidRepository.findAllByAuctionId(auctionId);
    }

    public BidResponseDto toBidResponseDto(Bid bid) {
        return BidResponseDto.builder()
                .bidId(bid.getId())
                .auctionId(bid.getAuction().getId())
                .bidderEmail(bid.getBidder().getEmail())
                .bidPrice(bid.getBidPrice())
                .createdAt(bid.getCreatedAt())
                .build();
    }
}
