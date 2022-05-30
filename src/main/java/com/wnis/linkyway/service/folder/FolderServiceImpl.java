package com.wnis.linkyway.service.folder;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("folderService")
@RequiredArgsConstructor
@Transactional
public class FolderServiceImpl implements FolderService {
    
    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;
    
    @Override
    public Response<FolderResponse> findFolderSuper(Long memberId) {
        Folder folder = folderRepository.findFolderByMemberId(memberId).orElseThrow(()->
                new ResourceNotFoundException("해당 회원의 최상위 폴더를 조회 할 수 없습니다"));
        FolderResponse folderResponse = new FolderResponse(folder);
        return Response.of(HttpStatus.OK, folderResponse, "폴더 조회 성공");
    }
    
    @Override
    public Response<FolderResponse> findFolder(Long folderId) {
        Folder folder = folderRepository.findFolderById(folderId).orElseThrow(() ->
             new ResourceConflictException("해당 회원의 폴더가 존재하지 않아 조회 할 수 없습니다.")
        );
        FolderResponse folderResponse = new FolderResponse(folder);
        return Response.of(HttpStatus.OK, folderResponse, "폴더를 성공적으로 조회했습니다.");
    }
    
    @Override
    public Response<FolderResponse> addSuperFolder(Long memberId) {
        folderRepository.addSuperFolder(memberId);
        return Response.of(HttpStatus.OK, null, "default 폴더 생성 성공");
    }
    
    @Override
    public Response<FolderResponse> addFolder(AddFolderRequest addFolderRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new ResourceConflictException("회원이 존재하지 않습니다.")
        );
        Folder parent = folderRepository.findFolderById(addFolderRequest.getParentFolderId()).orElseThrow(() ->
            new ResourceConflictException("존재 하지 않는 상위 폴더입니다.")
        );
        Folder folder = Folder.builder()
                .member(member)
                .name(addFolderRequest.getName())
                .depth(parent.getDepth() + 1)
                .parent(parent)
                .build();
        
        folderRepository.save(folder);
        FolderResponse response = FolderResponse.builder()
                .folderId(folder.getParent().getId())
                .build();
        return Response.of(HttpStatus.OK, response, "폴더 생성 성공");
    }
    
    @Override
    public Response<FolderResponse> setFolderPath(SetFolderPathRequest setFolderPathRequest, Long folderId) {
        Folder folder = folderRepository.findFolderById(folderId).orElseThrow(() ->
            new ResourceConflictException("수정 하려는 폴더가 존재하지 않습니다.")
        );
        
        Folder destinationFolder = folderRepository.findFolderById(setFolderPathRequest.getTargetFolderId())
                .orElseThrow(() ->
                    new ResourceConflictException("목표 부모 폴더가 존재하지 않습니다.")
                );
        
        if (destinationFolder.isDirectAncestor(folder)) {
            throw new ResourceConflictException("직계 자손을 목표 부모 폴더로 지정 할 수 없습니다.");
        }
        folder.modifyParent(destinationFolder);
        return Response.of(HttpStatus.OK, null, "폴더 경로가 성공적으로 수정되었습니다.");
        
    }
    
    @Override
    public Response<FolderResponse> setFolderName(SetFolderNameRequest setFolderNameRequest, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceConflictException("해당 폴더가 존재하지 않아 수정을 할 수 없습니다."));
        
        folder.updateName(setFolderNameRequest.getName());
        return Response.of(HttpStatus.OK, null, "폴더 이름이 성공적으로 수정되었습니다.");
    }
    
    @Override
    public Response<FolderResponse> deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceConflictException("해당 폴더가 존재하지 않아 삭제 할 수 없습니다."));
        
        folderRepository.deleteById(folderId);
        return Response.of(HttpStatus.OK, null, "폴더와 하위 폴더가 성공적으로 삭제되었습니다.");
    }
    
    
}
