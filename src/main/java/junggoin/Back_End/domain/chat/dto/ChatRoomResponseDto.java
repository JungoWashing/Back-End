package junggoin.Back_End.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatRoomResponseDto {
    private final String roomId;
    private final String name;
    private final LocalDateTime lastMessageDate;
    private final String lastMessage;
    private final String imageUrl;
}
