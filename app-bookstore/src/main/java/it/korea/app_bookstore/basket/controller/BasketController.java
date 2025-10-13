package it.korea.app_bookstore.basket.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_bookstore.basket.dto.BasketDTO;
import it.korea.app_bookstore.basket.service.BasketService;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    /**
     * 장바구니 뷰
     * @param userId 유저 아이디
     * @return
     */
    @GetMapping("/detail/{userId}")
    public ModelAndView getBasketView(@PathVariable(name = "userId") String userId,
            @AuthenticationPrincipal UserSecureDTO user) throws Exception {

        if (!userId.equals(user.getUserId())) {   // 로그인 된 유저의 아이디와 PathVariable 로 넘어온 아이디가 일치하지 않을 경우...
            throw new RuntimeException("다른 사용자의 장바구니는 볼 수 없습니다.");
        }

        ModelAndView view = new ModelAndView();
        List<BasketDTO.Detail> dtoList = null;

        dtoList = basketService.getBasketList(userId);

        int totalPrice = 0;

        totalPrice = Optional.ofNullable(dtoList)
            .orElse(Collections.emptyList())
            .stream()
            .mapToInt(BasketDTO.Detail::getTotalPrice)
            .sum();

        view.addObject("vo", dtoList);
        view.addObject("userId", userId);
        view.addObject("totalPrice", totalPrice);
        view.setViewName("views/basket/detail");
        
        return view;
    }
}
