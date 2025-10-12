package it.korea.app_bookstore.book.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.book.dto.BookSearchDTO;
import it.korea.app_bookstore.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookApiController {

    private final BookService bookService;    

    /**
     * 도서 리스트 가져오기
     * @param pageable
     * @return
     * @throws Exception
     */
    @GetMapping("/book/list")
    public ResponseEntity<Map<String, Object>> getNoticeList(@PageableDefault(page = 0, size = 10,
        sort = "bookId", direction = Direction.DESC) Pageable pageable,
        BookSearchDTO searchDTO) throws Exception {

        Map<String, Object> resultMap = bookService.getBookList(pageable, searchDTO);
        
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * 도서 상세 정보 가져오기
     * @param bookId 도서 아이디
     * @return
     * @throws Exception
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Map<String, Object>> getBook(@PathVariable(name = "bookId") int bookId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        
        BookDTO.Detail dto = bookService.getBook(bookId);
        resultMap.put("vo", dto);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 도서 등록하기
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/book")
    public ResponseEntity<Map<String, Object>> createBook(@Valid @ModelAttribute BookDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = bookService.createBook(request);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 도서 수정하기
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping("/book")
    public ResponseEntity<Map<String, Object>> updateBook(@Valid @ModelAttribute BookDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = bookService.updateBook(request);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 도서 삭제하기
     * @param bookId 도서 아이디
     * @return
     * @throws Exception
     */
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable(name = "bookId") int bookId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = bookService.deleteBook(bookId);

        return new ResponseEntity<>(resultMap, status);
    }

    /**
     * 파일 삭제하기
     * @param bfId 파일 아이디
     * @return
     * @throws Exception
     */
    @DeleteMapping("/book/file/{bfId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable(name = "bfId") int bfId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = bookService.deleteFile(bfId);

        return new ResponseEntity<>(resultMap, status);
    }
}
