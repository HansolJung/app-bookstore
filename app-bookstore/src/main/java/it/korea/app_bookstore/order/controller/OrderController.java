package it.korea.app_bookstore.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.book.service.BookService;
import it.korea.app_bookstore.order.dto.OrderDTO;
import it.korea.app_bookstore.order.service.OrderService;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final BookService bookService;
    private final OrderService orderService;

    /**
     * 도서 주문 뷰
     * @return
     * @throws Exception 
     */
    @GetMapping("/{bookId}")
    public ModelAndView getOrderView(@PathVariable(name = "bookId") int bookId) throws Exception {
        ModelAndView view = new ModelAndView();
        BookDTO.Detail dto = bookService.getBook(bookId);

        view.addObject("vo", dto);
        view.setViewName("views/order/form");
        
        return view;
    }

    /**
     * 나의 주문 내역 리스트
     * @param pageable 페이징 객체
     * @return
     */
    @GetMapping("/list/{userId}")
    public ModelAndView getOrderListView(@PageableDefault(page = 0, size = 10, 
            sort = "orderDate", direction = Direction.DESC) Pageable pageable,
            @PathVariable(name = "userId") String userId,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {

        if (!userId.equals(user.getUserId())) {   // 로그인 된 유저의 아이디와 PathVariable 로 넘어온 아이디가 일치하지 않을 경우...
            throw new RuntimeException("다른 사용자의 주문 내역 리스트는 볼 수 없습니다.");
        }

        ModelAndView view = new ModelAndView();
        Map<String, Object> resultMap = new HashMap<>();

        resultMap = orderService.getMyOrderList(pageable, userId);

        view.addObject("data", resultMap);
        view.addObject("userId", userId);
        view.setViewName("views/order/orderList");
        
        return view;
    }

    /**
     * 나의 주문 상세 뷰
     * @param orderId 주문 아이디
     * @return
     */
    @GetMapping("/detail/{orderId}")
    public ModelAndView getOrderDetailView(@PathVariable(name = "orderId") int orderId,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {

        String userId = user.getUserId();

        ModelAndView view = new ModelAndView();
        OrderDTO.Detail dto = orderService.getOrder(orderId, userId);

        view.addObject("vo", dto);
        view.setViewName("views/order/orderDetail");
        
        return view;
    }
}
