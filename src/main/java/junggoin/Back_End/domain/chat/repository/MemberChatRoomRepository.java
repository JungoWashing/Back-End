package junggoin.Back_End.domain.chat.repository;


import junggoin.Back_End.domain.chat.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
