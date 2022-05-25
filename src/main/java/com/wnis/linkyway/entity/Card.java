package com.wnis.linkyway.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false)
    @Setter
    private Long id;

    @Column(name = "link")
    private String link;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "shareable")
    private Boolean shareable;

    ////********************연관 관게  ***************************/////
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_folder_id")
    private Folder folder;


    // ***** 1 : N *****
    @OneToMany(mappedBy = "card")
    private List<CardTag> cardTags = new ArrayList<>();


    @Builder
    private Card(String link, String title, String content, Boolean shareable, Folder folder) {
        this.link = link;
        this.title = title;
        this.content = content;
        this.shareable = shareable;
        this.folder = folder;
        if (folder != null)
            folder.getCards().add(this);
    }
}