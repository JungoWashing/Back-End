package junggoin.Back_End.domain.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecommendAuctionResponseDto {
    private List<String> representative_images;
}
