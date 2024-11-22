package junggoin.Back_End.domain.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BidDto {
    private final String email;
    private final Long auctionId;
    private final int price;
}
