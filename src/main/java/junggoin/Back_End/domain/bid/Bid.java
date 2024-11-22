package junggoin.Back_End.domain.bid;

import jakarta.persistence.*;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class Bid {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "bid_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member bidder;

    @Column(name = "bid_price")
    private int bidPrice;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Bid(Auction auction, Member bidder, int bidPrice ){
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = bidPrice;
    }
}
