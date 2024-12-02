package junggoin.Back_End.domain.chat;

import static jakarta.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import junggoin.Back_End.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ChatMessage implements Serializable {
    // ID를 자동 생성되도록 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 자동 생성
    private Long id;

    public enum MessageType {
        ENTER, TALK, JOIN
    }
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    private String message;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "roomId")
    @JsonBackReference
    private ChatRoom chatRoom;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(
            MessageType messageType,
            Member sender,
            String message,
            ChatRoom chatRoom
    ) {
        this.messageType = messageType;
        this.sender = sender;
        this.message = message;
        this.chatRoom = chatRoom;
    }
}

