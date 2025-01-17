package FBI.scm.handler;

import FBI.scm.dto.response.BasicResponse;
import FBI.scm.handler.exception.JoinException;
import FBI.scm.handler.exception.LoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class MemberExceptionHandler {

    // 회원가입 예외 처리
    @ExceptionHandler(JoinException.class)
    public ResponseEntity<BasicResponse<String>> handleJoinException(JoinException e) {
        BasicResponse<String> response = new BasicResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 로그인 예외 처리
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<BasicResponse<String>> handleLoginException(LoginException e) {
        BasicResponse<String> response = new BasicResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
