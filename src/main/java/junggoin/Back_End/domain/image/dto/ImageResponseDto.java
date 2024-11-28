package junggoin.Back_End.domain.image.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ImageResponseDto {
    Long auctionId;
    List<String> imageUrls;

    @Builder
    public ImageResponseDto(Long auctionId, List<String> imageUrls) {
        this.auctionId = auctionId;
        this.imageUrls = imageUrls;
    }
}
