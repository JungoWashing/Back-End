package junggoin.Back_End.domain.auction.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import junggoin.Back_End.domain.auction.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRepDto {
    private String productName;
    private String description;
    private int startingPrice;
    private LocalDateTime endTime;
    private Status status;
    private int immediatePurchasePrice;

    @Builder
    public ProductRepDto(String productName, String description, int startingPrice,
            LocalDateTime endTime, Status status, int immediatePurchasePrice) {
        this.productName = productName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.endTime = endTime;
        this.status = status;
        this.immediatePurchasePrice = immediatePurchasePrice;
    }
}
