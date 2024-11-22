package junggoin.Back_End.domain.bid.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidResDto {
    Long auctionId;
    int price;

    @Builder
    public BidResDto(Long auctionId, int price) {
        this.auctionId = auctionId;
        this.price = price;
    }
}
