package it.korea.app_bookstore.basket.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_bookstore.basket.dto.BasketDTO;
import it.korea.app_bookstore.basket.service.BasketService;
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
    public ModelAndView getBasketView(@PathVariable(name = "userId") String userId) {

        ModelAndView view = new ModelAndView();
        List<BasketDTO.Detail> dtoList = null;

        try {
            dtoList = basketService.getBasketList(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
