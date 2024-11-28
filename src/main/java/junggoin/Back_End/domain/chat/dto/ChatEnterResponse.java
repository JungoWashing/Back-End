package junggoin.Back_End.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatEnterResponse {
    private String senderEmail;
    private String senderNickname;
    private String message;

    @Builder
    public ChatEnterResponse(String senderEmail, String senderNickname, String message) {
        this.senderEmail = senderEmail;
        this.senderNickname = senderNickname;
        this.message = message;
    }
}
