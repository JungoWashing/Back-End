package junggoin.Back_End.domain.member.repository;

import junggoin.Back_End.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findMemberByEmail(String email);
    boolean existsMemberByNickname(String nickname);
}
