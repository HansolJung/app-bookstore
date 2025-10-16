package it.korea.app_bookstore.order.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import it.korea.app_bookstore.user.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kb_orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    private LocalDateTime orderDate;
    private int totalPrice;
    private String status;

    // 구매한 사용자 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
 
    // 주문 아이템 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 기본적으로 fetch = FetchType.LAZY
    //@Fetch(FetchMode.SUBSELECT) // N+1 문제를 해결하기 위한 설정, 주 엔티티를 조회한 후, 연관 엔티티들은 서브 쿼리(SUBSELECT)를 사용하여 한 번에 일괄적으로 조회하여 불필요한 추가 쿼리 발생을 막아줌.
    // 데이터가 적을 경우에만 해당 옵션을 사용할 것.
    private Set<OrderItemEntity> itemList = new HashSet<>();    // NPE 방어 코드

    // 주문 아이템 추가
    public void addItems(OrderItemEntity entity) {
        if (itemList == null) {
            this.itemList = new HashSet<>();
        }

        entity.setOrder(this);
        itemList.add(entity);
    } 
}
