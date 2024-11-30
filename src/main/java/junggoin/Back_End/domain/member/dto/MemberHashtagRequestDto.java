package junggoin.Back_End.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberHashtagRequestDto {
    private List<String> hashtags;
}
