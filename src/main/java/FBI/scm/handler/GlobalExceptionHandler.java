package FBI.scm.handler;

import FBI.scm.controller.MemberController;
import FBI.scm.dto.response.ErrorResponse;
import FBI.scm.handler.exception.InvalidTokenException;
import FBI.scm.handler.exception.JoinException;
import FBI.scm.handler.exception.LoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JoinException.class)
    public final ResponseEntity<ErrorResponse> handleJoinException(JoinException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(400, ex.getMessage(), "회원가입 실패");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginException.class)
    public final ResponseEntity<ErrorResponse> handleLoginException(LoginException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(401, ex.getMessage(), "로그인 실패");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(401, ex.getMessage(), "유효하지 않은 토큰");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(500, ex.getMessage(), "예상치 못한 오류 발생");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
