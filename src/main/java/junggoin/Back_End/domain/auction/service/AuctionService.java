package junggoin.Back_End.domain.auction.service;

import jakarta.persistence.EntityNotFoundException;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.Status;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import junggoin.Back_End.domain.auction.dto.ProductReqDto;
import junggoin.Back_End.domain.auction.exception.ClosedAuctionException;
import junggoin.Back_End.domain.auction.repository.AuctionRepository;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuctionService {

    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

    private final AuctionRepository auctionRepository;

    private final MemberService memberService;

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisLockRegistry redisLockRegistry;

    private static final String AUCTION_KEY_PREFIX = "Auction:ProductId:";

    // 경매 id로 경매 조회
    public Auction findById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 경매 : " + auctionId));
    }

    // 모든 경매 조회
    public List<Auction> findAll() {
        return auctionRepository.findAll();
    }

    // 이메일로 경매 조회(판매 내역 조회에 필요)
    public List<Auction> findByEmail(String email) {
        return auctionRepository.findByMemberEmail(email);
    }

    // Method to create an auction and set it in Redis with a TTL
    @Transactional
    public ProductRepDto createAuction(String email, ProductReqDto productReqDto) {
        Auction auction = createProduct(email, productReqDto);
        Timestamp currentTime = Timestamp.from(Instant.now());
        long durationInSeconds = calculateTimeDifference(currentTime,
                Timestamp.valueOf(auction.getExpiredAt())).getSeconds();
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String key = AUCTION_KEY_PREFIX + auction.getId();

        setAuctionExpire(key,pid,durationInSeconds);

        log.info("Auction started for product ID: " + auction.getId());

        return toProductRepDto(auction);
    }

    public void setAuctionExpire(String key, String pid, long durationInSeconds) {
        redisTemplate.opsForValue().set(key, pid);
        redisTemplate.expire(key, durationInSeconds, TimeUnit.SECONDS);
    }

    // 경매 삭제
    public Long deleteAuction(Long auctionId) {
        Auction auction = findById(auctionId);
        auctionRepository.delete(auction);
        return auctionId;
    }

    /*
    즉시구매 로직 작성
     */
    @Transactional
    public void immediatePurchase(Long auctionId) {
        String key = AUCTION_KEY_PREFIX + auctionId;
        String lockKey = String.format("ProductId:%d", auctionId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 경매 : " + auctionId));
        try {
            redisLockRegistry.executeLocked(lockKey, Duration.ofSeconds(5), () -> {
                if (auction.getStatus() == Status.CLOSED) {
                    throw new ClosedAuctionException("해당 경매가 종료되었습니다. auctionId : " + auctionId);
                }
                redisTemplate.expire(key, 0, TimeUnit.SECONDS);
                auction.updateStatus(Status.CLOSED);
            });
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Method to end an auction
    public void endAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 경매 : " + auctionId));
        String lockKey = String.format("ProductId:%d", auctionId);
        try {
            redisLockRegistry.executeLocked(lockKey, Duration.ofSeconds(5), () ->
                    auction.updateStatus(Status.CLOSED)
            );
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
        auctionRepository.save(auction);
        log.info("Auction ended for product ID: " + auctionId);
    }

    public void updateWinningPrice(Auction auction, int price) {
        auction.updateWinningPrice(price);
        auctionRepository.save(auction);
    }

    // Redis expiration listener
    public class RedisExpirationListener implements MessageListener {

        @Override
        public void onMessage(Message message, byte[] pattern) {
            String key = message.toString();
            if (key.startsWith(AUCTION_KEY_PREFIX)) {
                String productId = key.substring(AUCTION_KEY_PREFIX.length());
                endAuction(Long.valueOf(productId)); // End the auction transaction
            }
        }
    }

    // 경매 생성
    private Auction createProduct(String email, ProductReqDto productReqDto) {
        Member member = memberService.findMemberByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원: " + email));
        Auction auction = Auction.builder()
                .itemName(productReqDto.getProductName())
                .description(productReqDto.getDescription())
                .member(member)
                .startingPrice(productReqDto.getStartingPrice())
                .expiredAt(productReqDto.getEndTime())
                .immediatePurchasePrice(productReqDto.getImmediatePurchasePrice())
                .status(Status.BIDDING)
                .build();
        auctionRepository.save(auction);
        return auction;

    }


    private Duration calculateTimeDifference(Timestamp start, Timestamp end) {
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();

        Duration duration = Duration.between(startInstant, endInstant);

        if (duration.isNegative()) {
            throw new IllegalArgumentException("종료 시간이 시작 시간보다 빠릅니다.");
        }
        return duration;
    }

    public ProductRepDto toProductRepDto(Auction auction) {
        return ProductRepDto.builder()
                .auctionId(auction.getId())
                .sellerEmail(auction.getMember().getEmail())
                .productName(auction.getItemName())
                .immediatePurchasePrice(auction.getImmediatePurchasePrice())
                .highestBidPrice(auction.getWinningPrice())
                .startingPrice(auction.getStartingPrice())
                .description(auction.getDescription())
                .endTime(auction.getExpiredAt())
                .status(auction.getStatus().name())
                .build();
    }
}
