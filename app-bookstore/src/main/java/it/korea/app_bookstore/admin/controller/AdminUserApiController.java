package it.korea.app_bookstore.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_bookstore.admin.dto.AdminUserRequestDTO;
import it.korea.app_bookstore.admin.dto.AdminUserSearchDTO;
import it.korea.app_bookstore.admin.dto.AdminUserUpdateRequestDTO;
import it.korea.app_bookstore.admin.service.AdminUserService;
import it.korea.app_bookstore.order.dto.OrderSearchDTO;
import it.korea.app_bookstore.order.dto.OrderStatusDTO;
import it.korea.app_bookstore.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminUserApiController {

    private final AdminUserService userService;
    private final OrderService orderService;

    /**
     * 회원 리스트 요청
     * @param pageable 페이징 객체
     * @param searchDTO 검색 내용 DTO
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/user")
    public ResponseEntity<Map<String, Object>> getUserList(@PageableDefault(page = 0, size = 10, 
            sort = "createDate", direction = Direction.DESC) Pageable pageable,
            AdminUserSearchDTO searchDTO) throws Exception {
        
        Map<String, Object> resultMap = userService.getUserList(pageable, searchDTO);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * 회원 등록 요청
     * @param userRequestDTO 회원 등록 내용 DTO
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/admin/user")  
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody // JSON 타입으로 받기 때문에 @RequestBody 사용
            AdminUserRequestDTO userRequestDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try {

            userService.createUser(userRequestDTO);
            resultMap.put("resultCode", 200);
            resultMap.put("resultMsg", "OK");
        } catch (Exception e) {
            // 예외 발생 시 공통 모듈을 실행하기 위해 예외를 던진다
            throw new Exception(e.getMessage() == null ? "회원 등록 실패" : e.getMessage());
        }

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 회원 정보 수정 요청
     * @param userRequestDTO 회워 정보 수정 내용 DTO
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/admin/user")
    public ResponseEntity<Map<String, Object>> updateUser(@Valid @RequestBody // JSON 타입으로 받기 때문에 @RequestBody 사용
            AdminUserUpdateRequestDTO userRequestDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try {

            userService.updateUser(userRequestDTO);
            resultMap.put("resultCode", 200);
            resultMap.put("resultMsg", "OK");
        } catch (Exception e) {
            // 예외 발생 시 공통 모듈을 실행하기 위해 예외를 던진다
            throw new Exception(e.getMessage() == null ? "회원 수정 실패" : e.getMessage());
        }

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 회원 삭제 요청
     * @param userId 회원 아이디
     * @return
     * @throws Exception
     */
    @DeleteMapping("/admin/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable(name = "userId") String userId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try {

            userService.deleteUser(userId);
            resultMap.put("resultCode", 200);
            resultMap.put("resultMsg", "OK");
        } catch (Exception e) {
            // 예외 발생 시 공통 모듈을 실행하기 위해 예외를 던진다
            throw new Exception(e.getMessage() == null ? "회원 삭제 실패" : e.getMessage());
        }

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 주문 내역 리스트 요청
     * @param pageable 페이징 객체
     * @param searchDTO 검색 내용 DTO
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/order/list")
    public ResponseEntity<Map<String, Object>> getOrderList(@PageableDefault(page = 0, size = 10, 
            sort = "orderDate", direction = Direction.DESC) Pageable pageable,
            OrderSearchDTO searchDTO) throws Exception {
        
        Map<String, Object> resultMap = orderService.getOrderList(pageable, searchDTO);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * 주문 상태 변경 요청
     * @param orderStatusDTO 주문 상태 변경 내용 DTO
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/admin/order/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@Valid @RequestBody // JSON 타입으로 받기 때문에 @RequestBody 사용
            OrderStatusDTO statusDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try {

            orderService.updateStatus(statusDTO);
            resultMap.put("resultCode", 200);
            resultMap.put("resultMsg", "OK");
        } catch (Exception e) {
            // 예외 발생 시 공통 모듈을 실행하기 위해 예외를 던진다
            throw new Exception(e.getMessage() == null ? "주문 상태 변경 실패" : e.getMessage());
        }

        return new ResponseEntity<>(resultMap, status);
    }
}
