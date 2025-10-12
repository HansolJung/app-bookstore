package it.korea.app_bookstore.order.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.korea.app_bookstore.book.entity.BookEntity;
import it.korea.app_bookstore.book.repository.BookRepository;
import it.korea.app_bookstore.common.dto.PageVO;
import it.korea.app_bookstore.order.dto.OrderDTO;
import it.korea.app_bookstore.order.dto.OrderSearchDTO;
import it.korea.app_bookstore.order.dto.OrderStatusDTO;
import it.korea.app_bookstore.order.entity.OrderEntity;
import it.korea.app_bookstore.order.entity.OrderItemEntity;
import it.korea.app_bookstore.order.repository.OrderRepository;
import it.korea.app_bookstore.order.repository.OrderSearchSpecification;
import it.korea.app_bookstore.user.entity.UserEntity;
import it.korea.app_bookstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 주문 내역 리스트 가져오기
     * @param pageable 페이징 객체
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderList(Pageable pageable) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<OrderEntity> pageList = orderRepository.findAll(pageable);

        List<OrderDTO.Response> userList = pageList.getContent().stream().map(OrderDTO.Response::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int) pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content", userList);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());
        
        return resultMap;
    }

    /**
     * 주문 내역 리스트 가져오기 (with Axios 호출 or 검색)
     * @param pageable 페이징 객체
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderList(Pageable pageable, OrderSearchDTO searchDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<OrderEntity> pageList = null;

        OrderSearchSpecification searchSpecification = new OrderSearchSpecification(searchDTO);
        pageList = orderRepository.findAll(searchSpecification, pageable);

        List<OrderDTO.Response> userList = pageList.getContent().stream().map(OrderDTO.Response::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int) pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content", userList);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());
        
        return resultMap;
    }

    /**
     * 나의 주문 내역 리스트 가져오기
     * @param pageable 페이징 객체
     * @param userId 사용자 아이디
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMyOrderList(Pageable pageable, String userId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<OrderEntity> pageList = orderRepository.findAllByUser_userId(userId, pageable);

        List<OrderDTO.Response> userList = pageList.getContent().stream().map(OrderDTO.Response::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int) pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content", userList);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());
        
        return resultMap;
    }

    /**
     * 나의 주문 내역 리스트 가져오기 (with Axios 호출 or 검색)
     * @param pageable 페이징 객체
     * @param searchDTO 검색 객체
     * @param userId 사용자 아이디
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMyOrderList(Pageable pageable, OrderSearchDTO searchDTO, String userId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<OrderEntity> pageList = null;

        searchDTO.setUserId(userId);
        OrderSearchSpecification searchSpecification = new OrderSearchSpecification(searchDTO);
        pageList = orderRepository.findAll(searchSpecification, pageable);

        List<OrderDTO.Response> userList = pageList.getContent().stream().map(OrderDTO.Response::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int) pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content", userList);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());
        
        return resultMap;
    }

    /**
     * 주문 상세정보 가져오기
     * @param orderId 주문 아이디
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public OrderDTO.Detail getOrder(int orderId) throws Exception {
        return OrderDTO.Detail.of(orderRepository.getOrder(orderId)
            .orElseThrow(()-> new RuntimeException("주문 없음")));
    }

    /**
     * 도서 주문하기
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> orderBook(OrderDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<Integer, Object> bookMap = new HashMap<>();

        if (request.getBookList() == null || request.getBookList().size() == 0) {
            throw new RuntimeException("주문하려는 도서가 없음");
        }

        for (OrderDTO.InnerRequest innerRequest : request.getBookList()) {
            bookMap.put(innerRequest.getBookId(), innerRequest.getQuantity());
        }

        List<BookEntity> bookList = bookRepository.findAllById(bookMap.keySet());
        
        if (bookList == null || bookList.size() == 0) {
            throw new RuntimeException("해당 도서들 없음");
        }

        UserEntity userEntity = userRepository.findById(request.getUserId())
            .orElseThrow(()-> new RuntimeException("해당 유저 없음"));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUser(userEntity);
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setStatus("주문완료");
        
        int totalOrderPrice = 0;
        int originalDeposit = userEntity.getDeposit();

        for (BookEntity bookEntity : bookList) {
            int originalQuantity = bookEntity.getStock();
            int quantity = (int) bookMap.get(bookEntity.getBookId());
            
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

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }

    /**
     * 주문 상태 변경하기
     * @param orderId 주문 아이디
     * @throws Exception
     */
    @Transactional
    public void updateStatus(OrderStatusDTO statusDTO) throws Exception {

        OrderEntity orderEntity = orderRepository.findById(statusDTO.getOrderId())
            .orElseThrow(()-> new RuntimeException("주문 없음"));

        orderEntity.setStatus(statusDTO.getNewStatus());

        if ("주문취소".equals(statusDTO.getNewStatus())) {  // 주문취소일 경우...
            UserEntity userEntity = orderEntity.getUser();
            Set<OrderItemEntity> orderItemEntityList = orderEntity.getItemList();

            int originalDeposit = userEntity.getDeposit();
            int totalPrice = orderEntity.getTotalPrice();

            userEntity.setDeposit(originalDeposit + totalPrice);  // 주문이 취소되었기 때문에 보유금 원상복구

            for (OrderItemEntity orderItemEntity : orderItemEntityList) {
                BookEntity bookEntity = orderItemEntity.getBook();
                bookEntity.setStock(bookEntity.getStock() + orderItemEntity.getQuantity()); // 주문이 취소되었기 때문에 재고 원상복구
            }
        }

        orderRepository.save(orderEntity);
    }
}
