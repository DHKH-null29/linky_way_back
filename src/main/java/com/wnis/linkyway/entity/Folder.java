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
@Table(name = "folder")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id", nullable = false)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "depth")
    private Long depth;
    
    ////********************연관 관게  ***************************/////
    
    // ***** 1 : N *****
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Folder> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "folder", cascade = CascadeType.REMOVE)
    private List<Card> cards = new ArrayList<>();
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_member_id")
    private Member member;
    
    // ********연관관계 메소드 *************************
    
    // ***************** 생성자 ****************************
    @Builder
    private Folder(String name, Long depth, Folder parent, Member member) {
        this.name = name;
        this.depth = depth;
        
        this.parent = parent;
        if (parent != null)
            parent.getChildren().add(this);
        
        this.member = member;
        if (member != null) {
            member.getFolders().add(this);
        }
    }
    
    public void modifyParent(Folder destination) {
        if (destination.isDirectAncestor(this)) {
            throw new IllegalStateException("순환 참조 위험성이 있습니다.");
        }
        
        // 기존 부모 자식연결 부분에서 현재 폴더 엔티티 제거
        Folder parent = this.getParent();
        parent.getChildren().removeIf((f) -> f.getId() == this.getId());
        
        this.parent = destination; // 현재 폴더의 부모를 destination 설정
        destination.getChildren().add(this); // destination 자식에 현재 폴더 엔티티 추가
        
    }
    
    public boolean isDirectAncestor(Folder folder) {
        Folder currentFolder = this;
        Folder parent = currentFolder.getParent();
        while (parent != null) {
            if (parent.getId() == folder.getId()) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
    
    // **************** 접근자 ************************
    public Folder updateName(String name) {
        this.name = name;
        return this;
    }
}