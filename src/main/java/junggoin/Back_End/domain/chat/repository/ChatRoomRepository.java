package junggoin.Back_End.domain.chat.repository;

import java.util.Optional;
import junggoin.Back_End.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    Optional<ChatRoom> findChatRoomByName(String name);
}
