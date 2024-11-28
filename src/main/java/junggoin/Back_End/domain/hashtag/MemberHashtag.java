package junggoin.Back_End.domain.hashtag;

import jakarta.persistence.*;
import junggoin.Back_End.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Entity
public class MemberHashtag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;
}
