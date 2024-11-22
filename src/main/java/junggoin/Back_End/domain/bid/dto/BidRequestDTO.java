package junggoin.Back_End.domain.bid.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BidRequestDTO {
    private String email;
    private int price;
}
