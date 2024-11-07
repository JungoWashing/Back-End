package junggoin.Back_End.domain.chat.controller;

import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.dto.MessageDTO;
import junggoin.Back_End.domain.chat.dto.SendMessageRequestDTO;
import junggoin.Back_End.domain.chat.redis.RedisPublisher;
import junggoin.Back_End.domain.chat.service.ChatRoomService;
import junggoin.Back_End.domain.chat.service.ChatService;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomService chatRoomService;
    private final ChatService chatMessageService;
    private final MemberRepository memberRepository;

    @MessageMapping("/chat/message")
    public void sendMessage(@RequestBody SendMessageRequestDTO message) {
        ChatRoom chatRoom = chatRoomService.findRoomById(message.getRoomId());

        Member sender = memberRepository.findById(message.getMemberId()).orElseThrow();
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .message(message.getMessage())
                .sender(sender)
                .messageType(ChatMessage.MessageType.valueOf(message.getMessageType()))
                .build();

        chatRoomService.updateEntity(chatRoom.getRoomId(), chatMessage.getMessage());
        chatMessageService.saveMessage(chatMessage);


        // 메시지 Redis에 발행
        redisPublisher.publish(chatRoomService.getTopic(message.getRoomId()), MessageDTO.builder()
                .id(chatMessage.getId())
                .roomName(message.getRoomId())
                .build()
        );
    }
}
