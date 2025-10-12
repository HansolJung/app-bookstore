package it.korea.app_bookstore.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    /**
     * 루트 경로로 들어왔을 경우 home.html 로 보냄
     * @return
     */
    @GetMapping({"", "/"})
    public ModelAndView getHomeView() {
        
        ModelAndView view = new ModelAndView();
        view.setViewName("views/home");

        return view;
    }
}
