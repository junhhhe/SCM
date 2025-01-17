package FBI.scm.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // HTTP 401 상태 코드 반환
public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String message) {
        super(message);
    }
}
