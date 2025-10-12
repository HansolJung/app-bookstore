package it.korea.app_bookstore.book.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.korea.app_bookstore.book.entity.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity>{


    @EntityGraph(attributePaths = {"fileList", "category"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<BookEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"fileList", "category"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<BookEntity> findAll(Specification<BookEntity> searchSpecification, Pageable pageable);

    @EntityGraph(attributePaths = {"fileList", "category"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<BookEntity> findAllByDelYn(String delYn, Pageable pageable);
    
    // fetch join 사용해서 N + 1 문제 해결
    @Query(value = """
        select b from BookEntity b left join fetch b.fileList where b.bookId =:bookId
        """)
    Optional<BookEntity> getBook(@Param("bookId") int bookId);
}
