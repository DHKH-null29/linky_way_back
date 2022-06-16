package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("select f from Folder f left outer join fetch f.parent " + "where f.id = :folderId")
    public Optional<Folder> findFolderById(@Param("folderId") Long folderId);

    @Query("select f from Folder f left join fetch f.parent join f.member "
            + "where f.member.id = :memberId and f.depth <= 1 ")
    public List<Folder> findAllSuperFolder(@Param("memberId") Long memberId);

    @Modifying
    @Query(value = "insert into folder (name, depth, parent_folder_id, member_member_id) "
            + "values ('default', 0, null, :memberId)",
           nativeQuery = true)
    public void addSuperFolder(@Param("memberId") Long memberId);

    @Query("select f from Folder f left outer join fetch f.parent "
            + "where f.id = :folderId and f.member.id = :memberId")
    public Optional<Folder> findByIdAndMemberId(@Param("memberId") Long memberId, @Param("folderId") Long folderId);
}