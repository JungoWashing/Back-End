package junggoin.Back_End.domain.chat.service;

import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.redis.RedisSubscriber;
import junggoin.Back_End.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisSubscriber redisSubscriber;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    // 채팅방에 대한 토픽을 저장할 맵
    private final Map<String, ChannelTopic> topics = new HashMap<>();

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public Optional<ChatRoom> findRoomById(String roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = ChatRoom.of(name);
        System.out.println("채팅방 생성: " + name);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    // Check if the room exists
    public boolean doesRoomExist(String roomId) {
        Optional<ChatRoom> room = chatRoomRepository.findChatRoomByName(roomId);
        if(room.isPresent()) return true;
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

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
