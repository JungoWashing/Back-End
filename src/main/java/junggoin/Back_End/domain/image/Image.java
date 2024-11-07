package junggoin.Back_End.domain.image;

import jakarta.persistence.*;
import junggoin.Back_End.domain.auction.Auction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Lob
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Builder
    public Image(byte[] data, Auction auction) {
        this.data = data;
        this.auction = auction;
    }
}
