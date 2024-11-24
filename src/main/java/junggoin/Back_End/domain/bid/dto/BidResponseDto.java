package junggoin.Back_End.domain.bid.dto;

import lombok.Builder;
import java.time.LocalDateTime;


public record BidResponseDto(Long bidId, Long auctionId, String bidderEmail, int bidPrice, LocalDateTime createdAt) {
    @Builder
    public BidResponseDto {
    }
}
