package junggoin.Back_End.domain.member;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import junggoin.Back_End.domain.chat.MemberChatRoom;
import junggoin.Back_End.domain.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)  // @CreatedDate 작동을 위해서 추가
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime created_at;

    @Enumerated(STRING)
    private RoleType role;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

    @Builder
    public Member(String name, String nickname, String socialId, String email,
            String profileImageUrl, RoleType role, List<MemberChatRoom> memberChatRooms) {
        this.name = name;
        this.nickname = nickname;
        this.socialId = socialId;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.memberChatRooms = memberChatRooms;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateRole(RoleType newRole) {
        this.role = newRole;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
