package junggoin.Back_End.domain.chat.service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.Status;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.bid.Bid;
import junggoin.Back_End.domain.bid.service.BidService;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.MemberChatRoom;
import junggoin.Back_End.domain.chat.dto.ChatRoomResponseDto;
import junggoin.Back_End.domain.chat.dto.CreateChatRoomRequest;
import junggoin.Back_End.domain.chat.redis.RedisSubscriber;
import junggoin.Back_End.domain.chat.repository.ChatRoomRepository;
import junggoin.Back_End.domain.chat.repository.MemberChatRoomRepository;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
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
    private final MemberService memberService;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final AuctionService auctionService;
    private final BidService bidService;

    // 채팅방에 대한 토픽을 저장할 맵
    private final Map<String, ChannelTopic> topics = new HashMap<>();

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("room not found with id: " + roomId));
    }

    public List<ChatRoom> getChatRoomsByMember(String email) {
        Member member = memberService.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with email: " + email));

        return member.getMemberChatRooms().stream()
                .map(MemberChatRoom::getChatRoom)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoom createChatRoomByBuyer(CreateChatRoomRequest request) {
        Member buyer = memberService.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + request.getEmail()));

        Auction auction = auctionService.findById(request.getAuctionId());

        Member seller = auction.getMember();

        ChatRoom chatRoom = buildChatRoom(buyer, seller,auction);

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom1 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(buyer)
                .build();
        MemberChatRoom memberChatRoom2 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(seller)
                .build();
        memberChatRoomRepository.save(memberChatRoom1);
        memberChatRoomRepository.save(memberChatRoom2);
        auction.updateStatus(Status.SOLD_OUT);
        auction.setChatRoom(chatRoom);

        return chatRoom;
    }

    private ChatRoom buildChatRoom(Member member1, Member member2,Auction auction) {
        return ChatRoom.builder()
                .name(member1.getNickname() + ", " + member2.getNickname())
                .lastMessageDate(LocalDateTime.now())
                .lastMessage("")
                .auction(auction)
                .build();
    }

    @Transactional
    public ChatRoom createChatRoomBySeller(CreateChatRoomRequest request) {
        Member seller = memberService.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + request.getEmail()));

        Auction auction = auctionService.findById(request.getAuctionId());

        Bid bid = bidService.getWinnerBid(auction.getId());

        Member buyer = memberService.findMemberByEmail(bid.getBidder().getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + bid.getBidder().getEmail()));

        ChatRoom chatRoom = buildChatRoom(seller, buyer,auction);

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom1 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(seller)
                .build();
        MemberChatRoom memberChatRoom2 = MemberChatRoom.builder()
                .chatRoom(chatRoom)
                .member(buyer)
                .build();
        memberChatRoomRepository.save(memberChatRoom1);
        memberChatRoomRepository.save(memberChatRoom2);
        auction.updateStatus(Status.SOLD_OUT);
        auction.setChatRoom(chatRoom);

        return chatRoom;
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
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        if (chatRoom.getLastMessage().equals(lastMessage)) {
            chatRoom.update(UUID.randomUUID().toString());
            chatRoomRepository.flush();
        }
        chatRoom.update(lastMessage);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

    public ChatRoomResponseDto toChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageDate(chatRoom.getLastMessageDate())
                .imageUrl(chatRoom.getAuction() != null &&
                        chatRoom.getAuction().getImageUrls() != null &&
                        !chatRoom.getAuction().getImageUrls().isEmpty()
                        ? chatRoom.getAuction().getImageUrls().get(0)
                        : null)
                .build();
    }
}
