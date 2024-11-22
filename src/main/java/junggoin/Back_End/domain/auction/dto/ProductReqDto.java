package junggoin.Back_End.domain.auction.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReqDto {
    private String productName;
    private String description;
    private int startingPrice;
    private int immediatePurchasePrice;
    private LocalDateTime endTime;
}
