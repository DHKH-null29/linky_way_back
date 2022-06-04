package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
class FolderRepositoryTest {
    
    private final Logger logger = LoggerFactory.getLogger(FolderRepositoryTest.class);
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;
    
    @BeforeEach
    void setup() {
        
        Member member1 = Member.builder()
                .email("maee@naver.com")
                .nickname("sssee")
                .password("a!aA212341")
                .build();
        
        
        Folder folder1 = Folder.builder()
                .member(member1)
                .depth(1L)
                .name("f1")
                .build();
        
        Folder folder2 = Folder.builder()
                .member(member1)
                .depth(2L)
                .name("f2")
                .parent(folder1)
                .build();
        
        Folder folder3 = Folder.builder()
                .member(member1)
                .depth(3L)
                .name("f3")
                .parent(folder2)
                .build();
        
        Folder folder4 = Folder.builder()
                .member(member1)
                .depth(3L)
                .name("f4")
                .parent(folder2)
                .build();
        
        Folder folder6 = Folder.builder()
                .member(member1)
                .depth(2L)
                .name("f6")
                .parent(folder1)
                .build();
        
        memberRepository.save(member1);
        folderRepository.saveAll(Arrays.asList(folder1, folder2, folder3, folder4, folder6));
    }
    
    @Test
    @DisplayName("findFolderByFolderId 테스트")
    void findFolderByIdTest() {
        Folder folder = folderRepository.findFolderById(2L).get();
        logger.info("폴더 id = {}, 폴더 이름 = {}, 폴더 부모 = {}, 폴더 자식 = {}",
                folder.getId(), folder.getName(), folder.getParent(), folder.getChildren());
        
        assertThat(folder).isInstanceOfSatisfying(Folder.class, (f) -> {
            assertThat(f.getId()).isEqualTo(2);
            assertThat(f.getName()).isEqualTo("f2");
            assertThat(f.getParent().getId()).isEqualTo(1);
            assertThat(f.getChildren().size()).isEqualTo(2);
        });
    }
    
    @Test
    @DisplayName("폴더 추가 테스트")
    void insertFolder() {
        Folder parent = folderRepository.findFolderById(1L).get();
        assertThat(parent.getChildren().size()).isEqualTo(2);
        
        Folder newFolder = Folder.builder()
                .parent(parent)
                .name("f5")
                .build();
        
        
        folderRepository.save(newFolder);
//        folderRepository.save(parent);
        folderRepository.flush();
        entityManager.clear();
        
        parent = folderRepository.findFolderById(1L).get();
        assertThat(parent.getChildren().size()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("폴더 경로 수정 테스트")
    void modifyMyParent() {
        entityManager.clear();
        
        Folder folder2 = folderRepository.findFolderById(2L).get();
        Folder folder6 = folderRepository.findFolderById(5L).get();
        
        
        folder2.modifyParent(folder6);
        
        // 트랜잭션 전에 DB로 바로 flush
        folderRepository.saveAndFlush(folder2);
//        folderRepository.saveAndFlush(folder6);
        
        // 영속성 컨테이너가 깨끗한 상태에서 DB에 수정이 반영되었는지 테스트
        entityManager.clear();
        
        Folder newFolder1 = folderRepository.findFolderById(1L).get();
        Folder newFolder2 = folderRepository.findFolderById(2L).get();
        Folder newFolder6 = folderRepository.findFolderById(5L).get();
        
        logger.info("폴더2 부모 이름: {}", newFolder2.getParent().getName());
        logger.info("폴더6 자식 이름: {}", newFolder6.getChildren().get(newFolder6.getChildren().size() - 1).getName());
        logger.info("폴더1 자식 이름: {}", newFolder1.getChildren().get(0).getName());
        
        assertThat(newFolder2.getParent().getName()).isEqualTo("f6");
        assertThat(newFolder6.getChildren().get(0).getId()).isEqualTo(2L);
        
    }
    
    @Test
    @DisplayName("폴더 경로 수정 실패 테스트")
    void failModifyMyParent() {
        entityManager.clear();
        
        Folder folder2 = folderRepository.findFolderById(2L).get();
        Folder folder4 = folderRepository.findFolderById(4L).get();
        
        Assertions.assertThatThrownBy(() -> folder2.modifyParent(folder4)).isInstanceOf(IllegalStateException.class);
        
    }
    
    @Test
    @DisplayName("폴더 이름 수정 테스트")
    void modifyFolderName() {
        Folder folder = folderRepository.findFolderById(1L).get();
        folder.updateName("hello");
        folderRepository.saveAndFlush(folder);
        entityManager.clear();
        
        Folder newFolder = folderRepository.findFolderById(1L).get();
        assertThat(newFolder.getName()).isEqualTo("hello");
    }
    
    
    @Test
    @DisplayName("폴더 삭제 테스트")
    void deleteFolders() {
        // cascade remove: 부모 객체가 날라가면 자식 객체 또한 날려버린다.
        // 현재 객체가 날라간다고 부모객체를 날리진 않는다. 테스트 완료
        folderRepository.deleteById(2L);
        folderRepository.flush();
        entityManager.clear();
        
        Folder folder3 = folderRepository.findById(3L).orElse(null);
        
        assertThat(folder3).isNull();
        assertThat(memberRepository.findById(1L).orElse(null)).isNotNull();
        
        Folder folder1 = folderRepository.findById(1L).orElse(null);
        assertThat(Objects.requireNonNull(folder1).getMember()).isNotNull();
    }
    
}