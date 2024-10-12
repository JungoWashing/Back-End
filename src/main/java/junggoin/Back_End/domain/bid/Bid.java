package junggoin.Back_End.domain.bid;

import jakarta.persistence.*;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
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
}
