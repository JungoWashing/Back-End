package junggoin.Back_End.domain.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ChatRoom implements Serializable {

    @Id
    @Column(name = "room_id")
    private String roomId;
    private String name;
    @LastModifiedDate //엔티티 수정될 때 마다 업데이트
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageDate;
    private String lastMessage;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // 순환 참조 관리
    private List<ChatMessage> chats = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

    @Builder
    public ChatRoom (String name, LocalDateTime lastMessageDate, String lastMessage) {
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.lastMessage = lastMessage;
        this.roomId = UUID.randomUUID().toString();
    }

    public void update(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
