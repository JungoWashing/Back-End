package junggoin.Back_End.domain.bid.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidResponseDTO {
    Long bidId;
    Long auctionId;
    Long bidderId;
    int price;

    @Builder
    BidResponseDTO(Long bidId, Long auctionId, Long bidderId, int price){
        this.bidId = bidId;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.price = price;
    }
}
