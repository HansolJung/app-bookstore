package it.korea.app_bookstore.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.korea.app_bookstore.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer>, JpaSpecificationExecutor<OrderEntity> {

    @EntityGraph(attributePaths = {"user", "itemList"})   // N+1 현상 해결
    Page<OrderEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "itemList"})   // N+1 현상 해결
    Page<OrderEntity> findAll(Specification<OrderEntity> searchSpecification, Pageable pageable);

    @EntityGraph(attributePaths = {"itemList", "itemList.book", "itemList.book.fileList"})   // N+1 현상 해결 (단, 도서 정보 까지만 해결 보장)
    Page<OrderEntity> findAllByUser_userId(String userId, Pageable pageable);

    // fetch join 사용해서 N + 1 문제 해결
    @Query("""
        select distinct o from OrderEntity o
        left join fetch o.user u
        left join fetch o.itemList i
        left join fetch i.book b
        left join fetch b.category c
        left join fetch b.fileList f
        where o.orderId = :orderId
    """)
    Optional<OrderEntity> getOrder(@Param("orderId") int orderId);
}
