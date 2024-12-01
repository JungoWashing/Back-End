package junggoin.Back_End.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HighestBidResponse {
    private String email;
    private String nickname;
    private int price;

    @Builder
    public  HighestBidResponse(String email, String nickname, int price) {
        this.email = email;
        this.nickname = nickname;
        this.price = price;
    }
}
