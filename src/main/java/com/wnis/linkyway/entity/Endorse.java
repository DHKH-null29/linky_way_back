package com.wnis.linkyway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "endorse")
public class Endorse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endorse_id", nullable = false)
    private Long id;

    //// ********************연관 관게 ***************************/////
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_tag_id")
    private Tag tag;

    @Builder
    public Endorse(Member member, Tag tag) {
        this.member = member;
        if (member != null) {
            member.getEndorses()
                  .add(this);
        }
        this.tag = tag;
        if (tag != null) {
            tag.getEndorses()
               .add(this);
        }
    }
}