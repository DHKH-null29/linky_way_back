package com.wnis.linkyway.service.folder;

import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
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
    
    @Value("${folder.limit.num}")
    private Long limitNumberOfFolder;
    
    @Value("${folder.limit.depth}")
    private Long limitDepth;

    @Override
    public List<FolderResponse> findAllFolderSuper(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundEntityException("존재하지 않는 회원입니다");
        }
        List<Folder> folderList = folderRepository.findAllSuperFolder(memberId);
        List<FolderResponse> response = new ArrayList<>();
        for (Folder folder : folderList) {
            FolderResponse folderResponse = new FolderResponse(folder);
            response.add(folderResponse);

        }
        return response;
    }

    @Override
    public FolderResponse findFolder(Long folderId, Long memberId) {
        Folder folder = folderRepository.findFolderById(folderId)
                .orElseThrow(() -> new NotFoundEntityException(
                        "존재하지 않는 폴더입니다."));
        checkRequestMyFolder(memberId, folder,  "존재하지 않는 폴더입니다.");
        return new FolderResponse(folder);
    }

    @Override
    public FolderResponse addFolder(AddFolderRequest addFolderRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException("회원이 존재하지 않습니다"));
    
        long numOfSuperFolder = folderRepository.countSuperFolderByMemberId(memberId); // 상위 폴더 갯수
        if (numOfSuperFolder > limitNumberOfFolder) {
            throw new LimitAddException(String.format("%d 초과로 상위 폴더를 생성 할 수 없습니다", limitNumberOfFolder));
        }
        
        long currentDepth = 1L;
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
        return FolderResponse.builder()
                .parentId(parentId)
                .folderId(folder.getId())
                .level(folder.getDepth())
                .name(folder.getName())
                .build();
    }

    @Override
    @Transactional
    public FolderResponse updateFolderPath(UpdateFolderPathRequest setFolderPathRequest, Long folderId, Long memberId) {
        Folder folder = folderRepository.findFolderById(folderId)
                .orElseThrow(() -> new NotModifyEmptyEntityException("수정 하려는 폴더가 존재하지 않습니다"));
        checkRequestMyFolder(memberId, folder, "수정 하려는 폴더가 존재하지 않습니다");
        if (setFolderPathRequest.getTargetFolderId() == null) {
            folder.deleteParent();
            return FolderResponse.builder()
                    .folderId(folder.getId())
                    .name(folder.getName())
                    .level(folder.getDepth())
                    .build();
        }

        Folder destinationFolder = folderRepository.findFolderById(setFolderPathRequest.getTargetFolderId())
                .orElseThrow(() -> new NotModifyEmptyEntityException(
                        "목표 부모 폴더가 존재하지 않아 수정 작업을 진행할 수 없습니다"));

        if (destinationFolder.isDirectAncestor(folder)) {
            throw new ResourceConflictException("직계 자손을 목표 부모 폴더로 지정 할 수 없습니다");
        }

        validSumOfFoldersDepth(folder, destinationFolder);
        folder.modifyParent(destinationFolder, destinationFolder.getDepth());

        Folder parent = folder.getParent();
        Long parentId = parent == null ? null : parent.getId();

        return FolderResponse.builder()
                .parentId(parentId)
                .folderId(folder.getId())
                .name(folder.getName())
                .level(folder.getDepth())
                .build();
    }

    private void validSumOfFoldersDepth(Folder targetFolder, Folder destinationFolder) {
        if (targetFolder.getDistanceOfFarthestChildren() + destinationFolder.getDepth() > limitDepth) {
            throw new LimitDepthException(String.format("깊이 %d 이상으로 디렉토리를 변경 할 수 없습니다", limitDepth));
        }
    }

    @Override
    public FolderResponse updateFolderName(UpdateFolderNameRequest updateFolderNameRequest, Long folderId, Long memberId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotModifyEmptyEntityException(
                        "해당 폴더가 존재하지 않아 수정을 할 수 없습니다"));
        checkRequestMyFolder(memberId, folder, "해당 폴더가 존재하지 않아 수정을 할 수 없습니다");
        folder.updateName(updateFolderNameRequest.getName());

        return FolderResponse.builder()
                .folderId(folderId)
                .name(folder.getName())
                .build();
    }

    @Override
    public FolderResponse deleteFolder(Long folderId, Long memberId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotDeleteEmptyEntityException(
                        "해당 폴더가 존재하지 않아 삭제 할 수 없습니다"));
        checkRequestMyFolder(memberId, folder, "해당 폴더가 존재하지 않아 삭제 할 수 없습니다");
        folderRepository.deleteById(folderId);

        return FolderResponse.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .build();
    }

    private void checkRequestMyFolder(long memberId, Folder folder, String message) {
        if (memberId != folder.getMember().getId()) {
            // 다른 status를 반환한다는 건 존재하는 자원이라는 걸 알려주는 것이기 때문에 그러면 안된다.
            throw new ResourceNotFoundException(message);
        }
    }

}
