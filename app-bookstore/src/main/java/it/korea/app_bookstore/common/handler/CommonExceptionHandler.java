package it.korea.app_bookstore.common.handler;

import java.nio.file.AccessDeniedException;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import it.korea.app_bookstore.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * Exception 핸들러
     * @param e Exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {

        if (isSecurityException(e)) {
            throw (RuntimeException) e;  // security 예외는 다시 던짐
        }

        String message = e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return judgeReturnType(request, message, err);
    }

    /**
     * RuntimeException 핸들러
     * @param e RuntimeException
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(Exception e, HttpServletRequest request) {

        if (isSecurityException(e)) {
            throw (RuntimeException) e;  // security 예외는 다시 던짐
        }

        String message = e.getMessage() != null && e.getMessage().length() > 0 ? e.getMessage() : "서버에 오류가 있습니다.";
        ErrorResponse err = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return judgeReturnType(request, message, err);
    }

    private boolean isSecurityException(Exception e) {
        return e instanceof AuthenticationException ||
            e instanceof AccessDeniedException ||
            e instanceof AuthenticationCredentialsNotFoundException;
    }

    /**
     * 리턴 타입 판별 메서드
     * @param request
     * @param message
     * @param err
     * @return
     */
    private Object judgeReturnType(HttpServletRequest request, String message, ErrorResponse err) {
        if (isAxios(request)) {  // axios 요청이라면...
            // ResponseEntity 로 돌려주기
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        } else {
            // 아니라면 ModelAndView 에러 페이지로 돌려주기
            ModelAndView view = new ModelAndView("views/error/errorPage");
            view.addObject("message", message);
            
            return view;
        }
    }

    private boolean isAxios(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
