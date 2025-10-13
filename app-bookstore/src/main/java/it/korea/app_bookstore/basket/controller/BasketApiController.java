package it.korea.app_bookstore.basket.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_bookstore.basket.dto.BasketDTO;
import it.korea.app_bookstore.basket.service.BasketService;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BasketApiController {

    private final BasketService basketService;

    /**
     * 장바구니에 도서 추가하기
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/basket")
    public ResponseEntity<Map<String, Object>> addBook(@Valid @RequestBody BasketDTO.Request request,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        
        request.setUserId(user.getUserId());
        resultMap = basketService.addBook(request);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 장바구니 전부 주문하기
     * @param userId 사용자 아이디
     * @return
     */
    @PostMapping("/basket/order/{userId}")
    public ResponseEntity<Map<String, Object>> orderAllBooks(@PathVariable(name = "userId") String userId,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {

        if (!userId.equals(user.getUserId())) {   // 로그인 된 유저의 아이디와 PathVariable 로 넘어온 아이디가 일치하지 않을 경우...
            throw new RuntimeException("다른 사용자의 장바구니는 주문할 수 없습니다.");
        }

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = basketService.orderAllBooks(userId);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 장바구니에서 도서 삭제하기
     * @param basketId 장바구니 아이디
     * @return
     */
    @DeleteMapping("/basket/{basketId}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable(name = "basketId") int basketId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = basketService.deleteBook(basketId);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 장바구니 전부 비우기
     * @param userId 사용자 아이디
     * @return
     */
    @DeleteMapping("/basket/empty/{userId}")
    public ResponseEntity<Map<String, Object>> deleteAllBooks(@PathVariable(name = "userId") String userId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = basketService.deleteAllBooks(userId);

        return new ResponseEntity<>(resultMap, status);
    }
}
