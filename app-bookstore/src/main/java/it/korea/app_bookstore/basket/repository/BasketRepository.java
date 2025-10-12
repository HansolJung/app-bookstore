package it.korea.app_bookstore.basket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.korea.app_bookstore.basket.entity.BasketEntity;

public interface BasketRepository extends JpaRepository<BasketEntity, Integer> {

    List<BasketEntity> findByUser_userId(String userId);
    void deleteByUser_userId(String userId);
    void deleteAllByBook_bookId(int bookId);
}
