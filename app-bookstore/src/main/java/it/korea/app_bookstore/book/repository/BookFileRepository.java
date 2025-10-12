package it.korea.app_bookstore.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.korea.app_bookstore.book.entity.BookFileEntity;

public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {
    
}
