package it.korea.app_bookstore.book.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.book.dto.CategoryDTO;
import it.korea.app_bookstore.book.service.BookService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    /**
     * 도서 리스트 뷰
     * @return
     */
    @GetMapping("/list")
    public ModelAndView getBookListView() throws Exception {
        ModelAndView view = new ModelAndView();
        List<CategoryDTO> list = bookService.getCategoryList();

        view.setViewName("views/book/list");
        view.addObject("vo", list);
        
        return view;
    }

    /**
     * 도서 상세 정보 뷰
     * @return
     * @throws Exception 
     */
    @GetMapping("/{bookId}")
    public ModelAndView getBookDetailView(@PathVariable(name = "bookId") int bookId) throws Exception {
        ModelAndView view = new ModelAndView();

        BookDTO.Detail dto = bookService.getBook(bookId);

        view.setViewName("views/book/detail");
        view.addObject("vo", dto);
        
        return view;
    }

    /**
     * 도서 등록 뷰
     * @return
     * @throws Exception 
     */
    @GetMapping("/create")
    public ModelAndView getCreateFormView() throws Exception {
        ModelAndView view = new ModelAndView();

        List<CategoryDTO> list = bookService.getCategoryList();

        view.setViewName("views/book/createForm");
        view.addObject("vo", list);
        
        return view;
    }

    /**
     * 도서 수정 뷰
     * @return
     * @throws Exception 
     */
    @GetMapping("/update/{bookId}")
    public ModelAndView getUpdateFormView(@PathVariable(name = "bookId") int bookId) throws Exception {
        ModelAndView view = new ModelAndView();

        BookDTO.Detail dto = bookService.getBook(bookId);
        List<CategoryDTO> list = bookService.getCategoryList();

        view.setViewName("views/book/updateForm");
        view.addObject("vo", dto);
        view.addObject("categories", list);
        
        return view;
    }
}
