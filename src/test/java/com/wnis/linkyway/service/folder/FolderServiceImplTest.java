package com.wnis.linkyway.service.folder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
@Import({FolderServiceImpl.class, ObjectMapper.class})
class FolderServiceImplTest {
    
    @Autowired
    FolderService folderService;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(FolderServiceImplTest.class);
    
    @BeforeEach
    void setup() {
        
        Member member1 = Member.builder()
                .email("maee@naver.com")
                .nickname("serin")
                .password("a!aA212341")
                .build();
        
        
        Folder folder1 = Folder.builder()
                .member(member1)
                .name("f1")
                .depth(1L)
                .build();
        
        Folder folder2 = Folder.builder()
                .member(member1)
                .name("f2")
                .depth(2L)
                .parent(folder1)
                .build();
        
        Folder folder3 = Folder.builder()
                .member(member1)
                .name("f3")
                .depth(3L)
                .parent(folder2)
                .build();
        
        Folder folder4 = Folder.builder()
                .member(member1)
                .name("f4")
                .depth(3L)
                .parent(folder2)
                .build();
        
        Folder folder6 = Folder.builder()
                .member(member1)
                .name("f6")
                .depth(2L)
                .parent(folder1)
                .build();
        
        memberRepository.save(member1);
        folderRepository.saveAll(Arrays.asList(folder1, folder2, folder3, folder4, folder6));
    }
    
    
    @Nested
    @DisplayName("폴더 조회")
    class FindFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        void Test() throws JsonProcessingException {
            Response<FolderResponse> folder = folderService.findFolder(1L);
            String s = objectMapper.writeValueAsString(folder);
            logger.info(s);
        }
        
        @Test
        @DisplayName("핸들링 테스트")
        void exceptionTest() {
            
            assertThatThrownBy(() -> folderService.findFolder(30L)).isInstanceOf(ResourceConflictException.class);
            
        }
        
    }
    
    @Nested
    @DisplayName("Default 폴더 추가")
    class AddSuperFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Response<FolderResponse> response = folderService.addSuperFolder(1L);
            List<Folder> folders = folderRepository.findFoldersByMemberId(1L);
            folders.forEach((f) -> logger.info("폴더 이름: {}", f.getName()));
            
            logger.info(objectMapper.writeValueAsString(response));
        }
    }
    
    
    @Nested
    @DisplayName("폴더 추가")
    class AddFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("뻐꾸기")
                    .build();
            
            Response<FolderResponse> folderResponseResponse = folderService.addFolder(addFolderRequest, 1L);
            String s = objectMapper.writeValueAsString(folderResponseResponse);
            logger.info(s);
        }
        
        @Test
        @DisplayName("예외 테스트1")
        void exceptionTest1() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(100L)
                    .name("뻐꾸기")
                    .build();
            
            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest, 1L))
                    .isInstanceOf(ResourceConflictException.class).hasMessage("존재 하지 않는 상위 폴더입니다.");
        }
        
        @Test
        @DisplayName("예외 테스트2")
        void exceptionTest2() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("뻐꾸기")
                    .build();
            
            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest, 100L))
                    .isInstanceOf(ResourceConflictException.class).hasMessage("회원이 존재하지 않습니다.");
        }
    }
    
    @Nested
    @DisplayName("폴더 경로 수정")
    class SetFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(2L).build();
            
            Response<FolderResponse> folderResponseResponse =
                    folderService.setFolderPath(setFolderPathRequest, 4L);
            
            String s = objectMapper.writeValueAsString(folderResponseResponse);
            logger.info(s);
        }
        
        @Test
        @DisplayName("예외 테스트1")
        void exceptionTest1() {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(200L).build();
            
            assertThatThrownBy(() -> folderService.setFolderPath(setFolderPathRequest, 4L))
                    .isInstanceOf(ResourceConflictException.class).hasMessage("목표 부모 폴더가 존재하지 않습니다.");
            
        }
        
        @Test
        @DisplayName("예외 테스트2")
        void exceptionTest2() {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(4L).build();
            
            assertThatThrownBy(() -> folderService.setFolderPath(setFolderPathRequest, 2L)).
                    isInstanceOf(ResourceConflictException.class)
                    .hasMessage("직계 자손을 목표 부모 폴더로 지정 할 수 없습니다.");
        }
    }
    
    @Nested
    @DisplayName("폴더 이름 수정")
    class SetFolderNameTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            SetFolderNameRequest setFolderNameRequest = SetFolderNameRequest.builder()
                    .name("스프링")
                    .build();
            
            Response<FolderResponse> folderResponseResponse = folderService.setFolderName(setFolderNameRequest, 1L);
            String s = objectMapper.writeValueAsString(folderResponseResponse);
            logger.info(s);
        }
        
        @Test
        @DisplayName("예외 테스트")
        void exceptionTest() {
            SetFolderNameRequest setFolderNameRequest = SetFolderNameRequest.builder()
                    .name("스프링")
                    .build();
            assertThatThrownBy(() -> folderService.setFolderName(setFolderNameRequest, 100L))
                    .isInstanceOf(ResourceConflictException.class).hasMessage("해당 폴더가 존재하지 않아 수정을 할 수 없습니다.");
        }
    }
    
    @Nested
    @DisplayName("폴더 삭제 테스트")
    class DeleteFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Response<FolderResponse> folderResponseResponse = folderService.deleteFolder(1L);
            String s = objectMapper.writeValueAsString(folderResponseResponse);
            logger.info(s);
        }
        
        @Test
        @DisplayName("예외 테스트")
        void exceptionTest() {
            assertThatThrownBy(() -> folderService.deleteFolder(100L)).
                    isInstanceOf(ResourceConflictException.class)
                    .hasMessage("해당 폴더가 존재하지 않아 삭제 할 수 없습니다.");
        }
    }
}