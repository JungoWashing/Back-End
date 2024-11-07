package junggoin.Back_End.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageDTO {
    Long id;
    String roomName;
    @Builder
    public MessageDTO(
            Long id,
            String roomName
    ) {
        this.id = id;
        this.roomName = roomName;
    }
}
