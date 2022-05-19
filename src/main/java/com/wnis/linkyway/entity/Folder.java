package com.wnis.linkyway.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "folder")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    ////********************연관 관게  ***************************/////

    // ***** 1 : N *****
    @OneToMany(mappedBy = "parent")
    private List<Folder> children = new ArrayList<>();

    @OneToMany(mappedBy = "folder")
    private List<Card> cards = new ArrayList<>();



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_member_id")
    private Member member;




    // ***************** 생성자 ****************************
    @Builder
    private Folder(String name, Folder parent, Member member) {
        this.name = name;

        this.parent = parent;
        if (parent != null)
            parent.getChildren().add(this);

        this.member = member;
        if (member != null) {
            member.getFolders().add(this);
        }
    }
}