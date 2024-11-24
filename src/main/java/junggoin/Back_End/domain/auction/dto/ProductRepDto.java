package junggoin.Back_End.domain.auction.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRepDto {
    private Long auctionId;
    private String sellerEmail;
    private String productName;
    private String description;
    private int startingPrice;
    private LocalDateTime endTime;
    private String status;
    private int immediatePurchasePrice;
    private int highestBidPrice;

    @Builder
    public ProductRepDto(Long auctionId, String sellerEmail,String productName, String description, int startingPrice,
            LocalDateTime endTime, String status, int immediatePurchasePrice, int highestBidPrice) {
        this.auctionId = auctionId;
        this.sellerEmail = sellerEmail;
        this.productName = productName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.endTime = endTime;
        this.status = status;
        this.immediatePurchasePrice = immediatePurchasePrice;
        this.highestBidPrice = highestBidPrice;
    }
}
