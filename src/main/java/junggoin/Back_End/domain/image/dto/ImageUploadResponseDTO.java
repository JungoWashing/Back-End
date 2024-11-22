package junggoin.Back_End.domain.image.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ImageUploadResponseDTO {
    Long auctionId;
    List<String> imageUrls;

    @Builder
    public ImageUploadResponseDTO(Long auctionId, List<String> imageUrls) {
        this.auctionId = auctionId;
        this.imageUrls = imageUrls;
    }
}
