package junggoin.Back_End.domain.bid.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequestDto {
    private String bidderEmail;
    private int price;
}
