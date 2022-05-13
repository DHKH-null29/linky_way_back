package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    @Query("select f from Folder as f left outer join fetch f.parent")
    public List<Folder> findAllEagerly();

    @Query("select f from Folder f join fetch f.member")
    public List<Folder> findAllIncludeMember();


}