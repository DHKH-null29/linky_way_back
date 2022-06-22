package com.wnis.linkyway.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Table(name = "card")
public class Card extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false)
    private Long id;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @ColumnDefault("false")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    //// ********************연관 관게 ***************************/////
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_folder_id", nullable = false)
    private Folder folder;

    // ***** 1 : N *****
    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    private List<CardTag> cardTags = new ArrayList<>();

    @Builder
    private Card(String link, String title, String content, Boolean isPublic, Boolean isDeleted, Folder folder) {
        this.link = link;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.folder = folder;
        this.isDeleted = isDeleted;
        if (folder != null)
            folder.getCards()
                  .add(this);
    }
    
    public Card(Long id, String link, String title, String content, Boolean isPublic, Folder folder) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.folder = folder;
        if (folder != null)
            folder.getCards()
                  .add(this);
    }

    public void updateLink(String link) {
        this.link = link;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void updateFolder(Folder folder) {
        this.folder = folder;
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
