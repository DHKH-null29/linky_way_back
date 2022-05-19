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
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "shareable")
    private Boolean shareable;

    @Column(name = "views")
    private Integer views;


    ////********************연관 관게  ***************************/////

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_member_id")
    private Member member;

    // ***** 1 : N *****
    @OneToMany(mappedBy = "tag")
    private List<CardTag> cardTags = new ArrayList<>();

    @OneToMany(mappedBy = "tag")
    private List<Ddabong> ddabongs = new ArrayList<>();


    //******************* setter ******************************////
    
    
    public Tag updateName(String name) {
        this.name = name;
        return this;
    }
    
    public Tag updateShareable(Boolean shareable) {
        this.shareable = shareable;
        return this;
    }
    
    public Tag updateViews(Integer views) {
        this.views = views;
        return this;
    }
    
    public Tag updateMember(Member member) {
        this.member = member;
        return this;
    }
    
    // **************** 생성자 *******************************///
    
    @Builder
    private Tag(String name, Boolean shareable, Integer views, Member member) {
        this.name = name;
        this.shareable = shareable;
        this.views = views;
        this.member = member;
        if (member != null) {
            member.getTags().add(this);
        }

    }
}