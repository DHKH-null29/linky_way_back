package com.wnis.linkyway.service.folder;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("folderService")
@RequiredArgsConstructor
@Transactional
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;

    @Value("${folder.limit.depth}")
    private Long limitDepth;

    @Override
    public Response<List<FolderResponse>> findAllFolderSuper(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("존재하지 않는 회원입니다");
        }
        List<Folder> folderList = folderRepository.findAllSuperFolder(memberId);
        List<FolderResponse> response = new ArrayList<>();
        for (Folder folder : folderList) {
            FolderResponse folderResponse = new FolderResponse(folder);
            response.add(folderResponse);

        }
        return Response.of(HttpStatus.OK, response, "폴더 조회 성공");
    }

    @Override
    public Response<FolderResponse> findFolder(Long folderId) {
        Folder folder = folderRepository.findFolderById(folderId)
                                        .orElseThrow(() -> new NotFoundEntityException(
                                                "해당 회원의 폴더가 존재하지 않아 조회 할 수 없습니다."));
        FolderResponse folderResponse = new FolderResponse(folder);
        return Response.of(HttpStatus.OK, folderResponse, "폴더를 성공적으로 조회했습니다.");
    }

    @Override
    public Response<FolderResponse> addFolder(AddFolderRequest addFolderRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new NotFoundEntityException("회원이 존재하지 않습니다"));

        Long currentDepth = 1L;
        Long parentId = null;
        Folder parent = null;

        if (addFolderRequest.getParentFolderId() != null) {
            parent = folderRepository.findFolderById(addFolderRequest.getParentFolderId())
                                     .orElseThrow(() -> new ResourceNotFoundException("해당 상위 폴더가 존재하지 않습니다"));
        }

        if (parent != null) {
            currentDepth = parent.getDepth() + 1;
            parentId = parent.getId();

            if (parent.getDepth() == 0L) {
                throw new ResourceConflictException("default 폴더에는 임의의 하위 폴더를 추가 할 수 없습니다");
            }
            if (currentDepth > limitDepth) {
                throw new LimitDepthException(
                        String.format("현재 폴더 깊이가 %d 를 초과하여 더 이상 깊이의 폴더를 추가 할 수 없습니다", limitDepth));
            }
        }

        Folder folder = Folder.builder()
                              .member(member)
                              .name(addFolderRequest.getName())
                              .depth(currentDepth)
                              .parent(parent)
                              .build();

        folderRepository.save(folder);
        FolderResponse response = FolderResponse.builder()
                                                .parentId(parentId)
                                                .folderId(folder.getId())
                                                .level(folder.getDepth())
                                                .build();
        return Response.of(HttpStatus.OK, response, "폴더 생성 성공");
    }

    @Override
    public Response<FolderResponse> setFolderPath(SetFolderPathRequest setFolderPathRequest, Long folderId) {
        Folder folder = folderRepository.findFolderById(folderId)
                                        .orElseThrow(() -> new NotModifyEmptyEntityException("수정 하려는 폴더가 존재하지 않습니다"));

        Folder destinationFolder = folderRepository.findFolderById(setFolderPathRequest.getTargetFolderId())
                                                   .orElseThrow(() -> new NotModifyEmptyEntityException(
                                                           "목표 부모 폴더가 존재하지 않아 수정 작업을 진행할 수 없습니다"));

        if (destinationFolder.isDirectAncestor(folder)) {
            throw new ResourceConflictException("직계 자손을 목표 부모 폴더로 지정 할 수 없습니다");
        }

        if (destinationFolder.getDepth() >= limitDepth) {
            throw new LimitDepthException(String.format("깊이가 %d 이상인 폴더의 하위 경로로 디렉토리를 변경 할 수 없습니다", limitDepth));
        }

        folder.modifyParent(destinationFolder);
        return Response.of(HttpStatus.OK, null, "폴더 경로가 성공적으로 수정되었습니다");

    }

    @Override
    public Response<FolderResponse> setFolderName(SetFolderNameRequest setFolderNameRequest, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                                        .orElseThrow(() -> new NotModifyEmptyEntityException(
                                                "해당 폴더가 존재하지 않아 수정을 할 수 없습니다"));

        folder.updateName(setFolderNameRequest.getName());
        return Response.of(HttpStatus.OK, null, "폴더 이름이 성공적으로 수정되었습니다");
    }

    @Override
    public Response<FolderResponse> deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                                        .orElseThrow(() -> new NotDeleteEmptyEntityException(
                                                "해당 폴더가 존재하지 않아 삭제 할 수 없습니다"));

        folderRepository.deleteById(folderId);
        return Response.of(HttpStatus.OK, null, "폴더와 하위 폴더가 성공적으로 삭제되었습니다");
    }

}
