package junggoin.Back_End.domain.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // ID를 자동 생성되도록 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 자동 생성
    private Long id;

    public enum MessageType {
        ENTER, TALK, JOIN
    }

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;
}
