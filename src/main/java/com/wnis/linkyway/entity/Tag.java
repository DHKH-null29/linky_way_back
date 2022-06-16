package com.wnis.linkyway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "tag",
indexes = {
        @Index(name = "tag_ix_name", columnList = "name"),
})
public class Tag extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Long id;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;
    
    //// ********************연관 관게 ***************************/////

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_member_id", nullable = false)
    private Member member;

    // ***** 1 : N *****
    @OneToMany(mappedBy = "tag", cascade = CascadeType.REMOVE)
    private List<CardTag> cardTags = new ArrayList<>();

    @OneToMany(mappedBy = "tag")
    private List<Endorse> endorses = new ArrayList<>();

    // ******************* setter ******************************////

    public Tag updateName(String name) {
        this.name = name;
        return this;
    }

    public Tag updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }
    

    public Tag updateMember(Member member) {
        this.member = member;
        return this;
    }

    // **************** 생성자 *******************************///

    @Builder
    private Tag(String name, Boolean isPublic, Member member) {
        this.name = name;
        this.isPublic = isPublic;
        this.member = member;
        if (member != null) {
            member.getTags()
                  .add(this);
        }

    }
}