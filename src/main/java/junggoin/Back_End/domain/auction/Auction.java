package junggoin.Back_End.domain.auction;

import jakarta.persistence.*;
import junggoin.Back_End.domain.bid.Bid;
import junggoin.Back_End.domain.image.Image;
import junggoin.Back_End.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
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

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @Column(name = "expired_at")
    @CreatedDate
    private LocalDateTime expiredAt;

    @Column(name = "winning_price")
    private int winningPrice;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @CreatedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @Getter
    private Member member;

    @Builder
    public Auction(Long id, String itemName, List<Bid> bids, String description, int startingPrice, int immediatePurchasePrice, LocalDateTime expiredAt, int winningPrice, Status status, Member member, List<Image> images) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.immediatePurchasePrice = immediatePurchasePrice;
        this.expiredAt = expiredAt;
        this.winningPrice = winningPrice;
        this.status = status;
        this.bids = bids;
        this.member = member;
        this.images = images;
    }
}
