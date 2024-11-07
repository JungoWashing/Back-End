package junggoin.Back_End.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder
public class SendMessageRequestDTO {
    private String roomId;
    private Long memberId;
    private String message;
    private String messageType;

    public SendMessageRequestDTO(@JsonProperty("roomId") String roomId, @JsonProperty("memberId") Long memberId, @JsonProperty("message") String message, @JsonProperty("messageType") String messageType) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.message = message;
        this.messageType = messageType;
    }
}
