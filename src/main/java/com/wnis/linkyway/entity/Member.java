package com.wnis.linkyway.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "password", nullable = false, length = 72)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    //// ********************연관 관게 ***************************/////

    // ***** 1 : N *****
    @OneToMany(mappedBy = "member")
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Endorse> endorses = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Tag> tags = new ArrayList<>();

    // ***************** setter ***************************

    public Member updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Member updatePassword(String password) {
        this.password = password;
        return this;
    }

    public Member updateEmail(String email) {
        this.email = email;
        return this;
    }

    public Member addTag(Tag tag) {
        this.tags.add(tag);
        tag.updateMember(this);
        return this;
    }

    // ***************** 생성자 ****************************
    @Builder
    private Member(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public void changePassword(String encode) {
        this.password = encode;
    }
}