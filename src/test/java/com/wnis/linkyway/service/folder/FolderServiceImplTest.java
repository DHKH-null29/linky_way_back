package com.wnis.linkyway.service.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.LimitAddException;
import com.wnis.linkyway.exception.common.LimitDepthException;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
@Import({FolderServiceImpl.class, ObjectMapper.class})
class FolderServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(FolderServiceImplTest.class);
    @Autowired
    FolderService folderService;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    MemberRepository memberRepository;


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
                .depth(0L)
                .build();

        Folder folder2 = Folder.builder()
                .member(member1)
                .name("f2")
                .depth(1L)
                .parent(null)
                .build();

        Folder folder3 = Folder.builder()
                .member(member1)
                .name("f3")
                .depth(2L)
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
    @DisplayName("최상위 폴더 조회")
    class FolderTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            List<FolderResponse> response = folderService.findAllFolderSuper(1L);
            response.forEach(folderResponse -> {
                assertThat(folderResponse.getFolderId()).isNotNull();
                assertThat(folderResponse.getName()).isNotNull();
                assertThat(folderResponse.getLevel()).isNotNull();
                assertThat(folderResponse.getChildFolderList()).isNotNull();
            });
        }

        @Test
        @DisplayName("없은 회원에 대해서 최상우 ㅣ폴더를 조회한 경우")
        void shouldThrowNotFoundException() {
            assertThatThrownBy(() -> {
                folderService.findAllFolderSuper(100L);
            }).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("폴더 조회")
    class FindFolderTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            FolderResponse folderResponse = folderService.findFolder(1L, 1L);
            assertThat(folderResponse.getFolderId()).isNotNull();
            assertThat(folderResponse.getName()).isNotNull();
            assertThat(folderResponse.getLevel()).isNotNull();
            assertThat(folderResponse.getChildFolderList()).isNotNull();

        }

        @Test
        @DisplayName("핸들링 테스트")
        void exceptionTest() {

            assertThatThrownBy(() -> folderService.findFolder(30L, 1L)).isInstanceOf(ResourceNotFoundException.class);

        }

    }

    @Nested
    @DisplayName("폴더 추가")
    class AddFolderTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(2L)
                    .name("뻐꾸기")
                    .build();

            FolderResponse folderResponse = folderService.addFolder(addFolderRequest, 1L);
            logger.info(folderResponse.getName());
            logger.info("folder level: {}", folderResponse.getLevel());
            logger.info("folder id: {}", folderResponse.getFolderId());
            assertThat(folderResponse.getFolderId()).isNotNull();
            assertThat(folderResponse.getName()).isNotNull();
            assertThat(folderResponse.getLevel()).isNotNull();
            assertThat(folderResponse.getChildFolderList()).isNotNull();

        }

        @Test
        @DisplayName("예외 테스트: 존재하지 않는 폴더를 입력한 경우")
        void exceptionTest1() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(100L)
                    .name("뻐꾸기")
                    .build();

            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest,
                    1L)).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("예외 테스트: 회원이 존재하지 않는 경우")
        void exceptionTest2() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("뻐꾸기")
                    .build();

            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest,
                    100L)).isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("회원이 존재하지 않습니다");
        }

        @Test
        @DisplayName("예외 테스트: 폴더 깊이가 M을 초과하는 경우")
        void exceptionTest3() {
            int maxFolderDepth = 3;
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(4L)
                    .name("뻐꾸기")
                    .build();

            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest,
                    1L)).isInstanceOf(LimitDepthException.class);
        }

        @Test
        @DisplayName("예외 테스트: default 폴더 아래에 임의로 생성하는 경우")
        void exceptionTest4() {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("뻐꾸기")
                    .build();

            assertThatThrownBy(() -> folderService.addFolder(addFolderRequest,
                    1L)).isInstanceOf(ResourceConflictException.class);
        }
    }

    @Nested
    @DisplayName("폴더 경로 수정")
    class UpdateFolderTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                    .targetFolderId(2L)
                    .build();

            FolderResponse folderResponse = folderService.updateFolderPath(updateFolderPathRequest, 4L, 1L);

            assertThat(folderResponse.getFolderId()).isNotNull();
            assertThat(folderResponse.getName()).isNotNull();
            assertThat(folderResponse.getLevel()).isNotNull();
            assertThat(folderResponse.getChildFolderList()).isNotNull();

        }

        @Test
        @DisplayName("예외 테스트1")
        void exceptionTest1() {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                    .targetFolderId(200L)
                    .build();

            assertThatThrownBy(() -> folderService.updateFolderPath(updateFolderPathRequest,
                    4L, 1L)).isInstanceOf(ResourceConflictException.class)
                    .hasMessage("목표 부모 폴더가 존재하지 않아 수정 작업을 진행할 수 없습니다");

        }

        @Test
        @DisplayName("예외 테스트2")
        void exceptionTest2() {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                    .targetFolderId(4L)
                    .build();

            assertThatThrownBy(() -> folderService.updateFolderPath(updateFolderPathRequest,
                    2L, 1L)).isInstanceOf(ResourceConflictException.class)
                    .hasMessage("직계 자손을 목표 부모 폴더로 지정 할 수 없습니다");
        }

        @Test
        @DisplayName("최상위 경로로의 이동 요청에 대한 동작 테스트")
        void changeRequestForSuperPathTest() {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder().build();
            FolderResponse folderResponse = folderService.updateFolderPath(updateFolderPathRequest, 3L, 1L);
            assertThat(folderResponse).isNotNull();
            assertThat(folderResponse.getParentId()).isNull();
            Folder folder3 = folderRepository.findFolderById(3L).orElse(null);
            assertThat(folder3).isNotNull();
            assertThat(folder3.getParent()).isNull();
        }
    }

    @Nested
    @DisplayName("폴더 이름 수정")
    class UpdateFolderNameTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            UpdateFolderNameRequest updateFolderNameRequest = UpdateFolderNameRequest.builder()
                    .name("스프링")
                    .build();

            FolderResponse folderResponse = folderService.updateFolderName(updateFolderNameRequest, 1L, 1L);

            assertThat(folderResponse.getName()).isNotNull();
            assertThat(folderResponse.getFolderId()).isNotNull();

        }

        @Test
        @DisplayName("예외 테스트")
        void exceptionTest() {
            UpdateFolderNameRequest updateFolderNameRequest = UpdateFolderNameRequest.builder()
                    .name("스프링")
                    .build();
            assertThatThrownBy(() -> folderService.updateFolderName(updateFolderNameRequest,
                    100L, 1L)).isInstanceOf(ResourceConflictException.class)
                    .hasMessage("해당 폴더가 존재하지 않아 수정을 할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("폴더 삭제 테스트")
    class DeleteFolderTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() {
            FolderResponse folderResponse = folderService.deleteFolder(1L, 1L);

            assertThat(folderResponse.getFolderId()).isNotNull();
            assertThat(folderResponse.getName()).isNotNull();
        }

        @Test
        @DisplayName("예외 테스트")
        void exceptionTest() {
            assertThatThrownBy(() -> folderService.deleteFolder(100L, 1L)).isInstanceOf(ResourceConflictException.class)
                    .hasMessage("해당 폴더가 존재하지 않아 삭제 할 수 없습니다");
        }
    }
    
    @Test
    @DisplayName("예외 테스트: 상위 폴더를 N개 이상 생성하는 경우")
    void shouldThrowLimitAddException_WhenCreateFolderOver() {
        assertThatThrownBy(()-> {
            for (int i = 0; i <= 7; ++i) {
                AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                    .name("뻐꾸기")
                                                                    .build();
                folderService.addFolder(addFolderRequest, 1L);
            }
        }).isInstanceOf(LimitAddException.class);
    }
    
}