package junggoin.Back_End.domain.chat.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.MemberChatRoom;
import junggoin.Back_End.domain.chat.redis.RedisSubscriber;
import junggoin.Back_End.domain.chat.repository.ChatRoomRepository;
import junggoin.Back_End.domain.chat.repository.MemberChatRoomRepository;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisSubscriber redisSubscriber;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    // 채팅방에 대한 토픽을 저장할 맵
    private final Map<String, ChannelTopic> topics = new HashMap<>();

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findChatRoomByName(roomId).orElseThrow(() -> new IllegalArgumentException("room not found with id: " + roomId));
    }

    public List<ChatRoom> getChatRoomsByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        return member.getMemberChatRooms().stream()
                .map(MemberChatRoom::getChatRoom)
                .collect(Collectors.toList());
    }

    public ChatRoom createChatRoom(Long memberId1, Long memberId2) {
        Member member1 = memberRepository.findById(memberId1)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId1));
        Member member2 = memberRepository.findById(memberId2)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId2));


        ChatRoom chatRoom = ChatRoom.builder()
                .name(member1.getNickname() + ", " + member2.getNickname())
                .lastMessageDate(LocalDateTime.now())
                .lastMessage("")
                .build();

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom1 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(member1)
                .build();
        MemberChatRoom memberChatRoom2 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(member2)
                .build();
        memberChatRoomRepository.save(memberChatRoom1);
        memberChatRoomRepository.save(memberChatRoom2);

        return chatRoom;
    }

    // Check if the room exists
    public boolean doesRoomExist(String roomId) {
        Optional<ChatRoom> room = chatRoomRepository.findChatRoomByName(roomId);
        if (room.isPresent()) return true;
        else return false;
    }

    // 채팅방에 들어갈 때 호출
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);

        // 해당 채팅방에 대한 토픽이 없으면 새로 생성하고 구독 설정
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    @Transactional
    public void updateEntity(String id, String lastMessage) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        if (chatRoom.getLastMessage().equals(lastMessage)) {
            chatRoom.update(UUID.randomUUID().toString());
            chatRoomRepository.flush();
        }
        chatRoom.update(lastMessage);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
