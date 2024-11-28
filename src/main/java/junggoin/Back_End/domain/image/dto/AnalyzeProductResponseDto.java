package junggoin.Back_End.domain.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnalyzeProductResponseDto {
    private String style1;
    private String style2;
    private String material;
    private String type;
    private String color;
}
