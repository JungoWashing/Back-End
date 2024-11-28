package junggoin.Back_End.domain.hashtag.repository;

import junggoin.Back_End.domain.hashtag.Hashtag;
import junggoin.Back_End.domain.hashtag.MemberHashtag;
import junggoin.Back_End.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHashtagRepository extends JpaRepository<MemberHashtag, Long> {
    MemberHashtag findByMemberAndHashtag(Member member, Hashtag hashtag);
}
