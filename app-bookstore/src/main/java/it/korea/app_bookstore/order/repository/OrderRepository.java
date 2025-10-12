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

    @EntityGraph(attributePaths = {"user", "itemList"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<OrderEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "itemList"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<OrderEntity> findAll(Specification<OrderEntity> searchSpecification, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "itemList"})   // 전체 유저 리스트 가져올 때 N+1 현상 해결
    Page<OrderEntity> findAllByUser_userId(String userId, Pageable pageable);

    // fetch join 사용해서 N + 1 문제 해결
    @Query(value = """
        select o from OrderEntity o left join fetch o.itemList where o.orderId =:orderId
        """)
    Optional<OrderEntity> getOrder(@Param("orderId") int orderId);
}
