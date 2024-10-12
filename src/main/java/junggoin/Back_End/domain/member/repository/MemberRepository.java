package junggoin.Back_End.domain.member.repository;

import junggoin.Back_End.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findMemberByName(String name);
}
