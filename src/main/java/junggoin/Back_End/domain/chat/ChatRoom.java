package junggoin.Back_End.domain.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom implements Serializable {

    @Id
    private String roomId;
    private String name;

    public static ChatRoom of(String name) {
        return ChatRoom.builder()
                .name(name)
                .roomId(UUID.randomUUID().toString())
                .build();
    }
}
