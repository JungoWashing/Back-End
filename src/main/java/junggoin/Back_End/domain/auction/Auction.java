package junggoin.Back_End.domain.auction;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import junggoin.Back_End.domain.bid.Bid;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class Auction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "auction_id")
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "description")
    private String description;

    @Enumerated(STRING)
    private Status status;

    @Column(name = "starting_price")
    private int startingPrice;

    @Column(name = "immediate_purchase_price")
    private int immediatePurchasePrice;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.REMOVE)
    @OrderBy(value = "bidPrice desc")
    private List<Bid> bids = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "auction_image",
            joinColumns = {@JoinColumn(name = "image_id", referencedColumnName = "auction_id")}
    )
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "winning_price")
    private int winningPrice;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @Getter
    private Member member;

    @OneToOne
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Builder
    public Auction(String itemName, List<Bid> bids, String description, int startingPrice,
            int immediatePurchasePrice, LocalDateTime expiredAt, int winningPrice, Status status,
            Member member) {
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.immediatePurchasePrice = immediatePurchasePrice;
        this.expiredAt = expiredAt;
        this.winningPrice = winningPrice;
        this.status = status;
        this.bids = bids;
        this.member = member;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateWinningPrice(int price) {
        this.winningPrice = price;
    }

    public void updateImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
