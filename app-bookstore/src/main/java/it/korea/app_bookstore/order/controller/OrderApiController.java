package it.korea.app_bookstore.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_bookstore.order.dto.OrderDTO;
import it.korea.app_bookstore.order.dto.OrderSearchDTO;
import it.korea.app_bookstore.order.service.OrderService;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderApiController {

    private final OrderService orderService;

    /**
     * 도서 주문하기
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> orderBook(@Valid @RequestBody OrderDTO.Request request,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        
        request.setUserId(user.getUserId());
        resultMap = orderService.orderBook(request);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 나의 주문 내역 리스트 요청
     * @param pageable 페이징 객체
     * @param searchDTO 검색 객체
     * @param userId 사용자 아이디
     * @return
     * @throws Exception
     */
    @GetMapping("/order/list/{userId}")
    public ResponseEntity<Map<String, Object>> getOrderList(@PageableDefault(page = 0, size = 10, 
            sort = "orderDate", direction = Direction.DESC) Pageable pageable,
            OrderSearchDTO searchDTO,
            @PathVariable(name = "userId") String userId) throws Exception {
        
        Map<String, Object> resultMap = orderService.getOrderList(pageable, searchDTO);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
