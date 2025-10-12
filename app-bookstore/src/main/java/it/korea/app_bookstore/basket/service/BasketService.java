package it.korea.app_bookstore.basket.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.korea.app_bookstore.basket.dto.BasketDTO;
import it.korea.app_bookstore.basket.entity.BasketEntity;
import it.korea.app_bookstore.basket.repository.BasketRepository;
import it.korea.app_bookstore.book.entity.BookEntity;
import it.korea.app_bookstore.book.repository.BookRepository;
import it.korea.app_bookstore.order.entity.OrderEntity;
import it.korea.app_bookstore.order.entity.OrderItemEntity;
import it.korea.app_bookstore.order.repository.OrderRepository;
import it.korea.app_bookstore.user.entity.UserEntity;
import it.korea.app_bookstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    /**
     * 장바구니 가져오기
     * @param userId 사용자 아이디
     * @return
     */
    @Transactional(readOnly = true)
    public List<BasketDTO.Detail> getBasketList(String userId) {

        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(()-> new RuntimeException("유저 없음"));
        List<BasketEntity> entityList = basketRepository.findByUser_userId(userId);

        if (entityList != null && entityList.size() > 0) {
            return entityList.stream().map(BasketDTO.Detail::of).toList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 장바구니 전부 주문하기
     * @param userId 사용자 아이디
     * @return
     */
    @Transactional
    public Map<String, Object> orderAllBooks(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(()-> new RuntimeException("유저 없음"));
        List<BasketEntity> entityList = basketRepository.findByUser_userId(userId);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUser(userEntity);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setStatus("주문완료");
        
        int totalOrderPrice = 0;
        int originalDeposit = userEntity.getDeposit();

        for (BasketEntity basketEntity : entityList) {
            BookEntity bookEntity = basketEntity.getBook();
            int originalQuantity = bookEntity.getStock();
            int quantity = basketEntity.getQuantity();
            
            if (quantity > originalQuantity) {  // 만약 주문하려는 책의 개수가 재고보다 많을 경우...
                throw new RuntimeException("주문 개수가 재고를 초과함");
            }

            bookEntity.setStock(originalQuantity - quantity);   // 해당 도서 재고에서 주문 개수 차감

            int totalItemPrice = quantity * bookEntity.getPrice();

            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setQuantity(quantity);
            orderItemEntity.setBook(bookEntity);
            orderItemEntity.setTotalPrice(totalItemPrice);
            
            orderEntity.addItems(orderItemEntity);

            totalOrderPrice += totalItemPrice;   // 총 주문 금액에 구매한 책 금액 더하기
        }

        if (totalOrderPrice > originalDeposit) { // 만약 총 주문 금액이 보유금보다 많을 경우...
            throw new RuntimeException("총 주문 금액이 보유금을 초과함");
        }

        orderEntity.setTotalPrice(totalOrderPrice);   // 총 주문 금액 저장
        userEntity.setDeposit(originalDeposit - totalOrderPrice);  // 보유금에서 총 주문 금액 차감
        orderRepository.save(orderEntity);

        basketRepository.deleteByUser_userId(userId);  // 장바구니 비우기

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }

    /**
     * 장바구니에 도서 추가하기
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> addBook(BasketDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        if (request.getBook() == null) {
            throw new RuntimeException("장바구니에 넣으려는 도서가 없음");
        }

        BookEntity bookEntity = bookRepository.findById(request.getBook().getBookId())
            .orElseThrow(()-> new RuntimeException("해당 도서 없음"));

        if (request.getBook().getQuantity() > bookEntity.getStock()) {
            throw new RuntimeException("장바구니에 넣으려는 도서의 개수가 재고를 초과함");
        }
        
        UserEntity userEntity = userRepository.findById(request.getUserId())
            .orElseThrow(()-> new RuntimeException("해당 유저 없음"));

        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setUser(userEntity);
        basketEntity.setBook(bookEntity);
        basketEntity.setQuantity(request.getBook().getQuantity());
        basketEntity.setTotalPrice(bookEntity.getPrice() * request.getBook().getQuantity());

        basketRepository.save(basketEntity);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }

    /**
     * 장바구니에서 도서 삭제하기
     * @param basketId 장바구니 아이디
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> deleteBook(int basketId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        basketRepository.deleteById(basketId);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }

    /**
     * 장바구니 전부 비우기
     * @param userId 사용자 아이디
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> deleteAllBooks(String userId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(()-> new RuntimeException("유저 없음"));
        List<BasketEntity> entityList = basketRepository.findByUser_userId(userId);

        for (BasketEntity entity : entityList) {
            basketRepository.delete(entity);
        }

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }
}
