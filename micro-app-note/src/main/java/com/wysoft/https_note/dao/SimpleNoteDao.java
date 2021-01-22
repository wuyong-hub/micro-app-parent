package com.wysoft.https_note.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wysoft.https_note.model.SimpleNote;

@Repository
public interface SimpleNoteDao extends JpaRepository<SimpleNote,Integer>{

    Page<SimpleNote> findAll(Specification<SimpleNote> spec, Pageable pageable);
}
