package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FolderTest {
    @Autowired
    FolderRepository folderRepository;


    /*
     feature: cascade persist 테스트
     Given: 폴더 엔티티 생성후 부모 자식 연결
     When: 폴더 저장
     Then: 부모 객체에서 자식객체와 실제 자식 객체와 같은지, 또는 반대의 경우도 같은지 확인
     */
    @Test
    void folderLazyTest() {
        Folder folder1 = Folder.builder()
                .name("parent")
                .parent(null)
                .build();


        Folder folder2 = Folder.builder()
                .name("child")
                .parent(folder1)
                .build();

        folderRepository.save(folder1);


        List<Folder> folders = folderRepository.findAll();
        for (Folder folder : folders) {
            System.out.println(folder.getName());
        }
        Folder p = folders.get(0);
        Folder c = folders.get(1);

        assertThat(p.getParent()).isNull();
//        assertThat(c.getParent()).isEqualTo(p); // 서로 다른 객체여도 내부 값이 같으면 OK
//        assertThat(c.getParent()).isSameAs(p); // 서로 같은 객체여야 함.
//        assertThat(p.getChildren().get(0)).isSameAs(c);
    }

    @Test
    void folderEagerTest() {
        Folder folder1 = Folder.builder()
                .name("parent")
                .parent(null)
                .build();


        Folder folder2 = Folder.builder()
                .name("child")
                .parent(folder1)
                .build();

        folderRepository.save(folder1);
        folderRepository.save(folder2);

        List<Folder> folders = folderRepository.findAllEagerly();
        for (Folder folder : folders) {
            System.out.println(folder.getName() + folder.getParent());
        }
        Folder p = folders.get(0);
        Folder c = folders.get(1);
//        assertThat(p.getCards()).isEmpty();
//        assertThat(p.getParent()).isNull();
        assertThat(c.getParent()).isEqualTo(p); // 서로 다른 객체여도 내부 값이 같으면 OK
        assertThat(c.getParent()).isSameAs(p); // 서로 같은 객체여야 함.
        assertThat(p.getChildren().get(0)).isSameAs(c);
    }



    /*
     feature: 폴더 객체와 카드 객체간 연관관계 접근 테스트
     Given: 폴더 카드 객체 생성후 연결
     When: repository를 이용해 저장
     Then: 카드 객체에서 자식 객체 또는 자식 객체에서 카드 객체 접근 확인
     */
    @Test
    void folderMemberRelationTest_N_1() {
        Folder folder1 = Folder.builder()
                .name("parent")
                .parent(null)
                .build();

        Member member = Member.builder()
                .nickname("이름")
                .email("marrin1101@nvaee.com")
                .build();

        Folder folder2 = Folder.builder()
                .name("child")
                .parent(folder1)
                .member(member)
                .build();

        folderRepository.save(folder2);

        List<Folder> folderList = folderRepository.findAllIncludeMember();
        assertThat(folderList.size()).isEqualTo(1);
        assertThat(folderList.get(0).getMember().getNickname()).isEqualTo(member.getNickname());
        assertThat(folderList.get(0).getName()).isEqualTo(folder2.getName());
        assertThat(folderList.get(0).getParent().getName()).isEqualTo("parent");
        assertThat(folderList.get(0).getMember().getFolders().size()).isEqualTo(1);

    }



}
