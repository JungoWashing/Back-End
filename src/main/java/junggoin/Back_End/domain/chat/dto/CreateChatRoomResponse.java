package junggoin.Back_End.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomResponse {

    private String roomId;
    private String name;

    @Builder
    public CreateChatRoomResponse(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }
}
