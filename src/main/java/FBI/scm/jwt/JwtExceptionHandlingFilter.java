package FBI.scm.jwt;

import FBI.scm.dto.response.ErrorResponse;
import FBI.scm.handler.UnAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public JwtExceptionHandlingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UnAuthorizedException ex) {
            handleException(response, HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", ex.getMessage());
        } catch (JwtException ex) {
            handleException(response, HttpStatus.UNAUTHORIZED, "INVALID_JWT_TOKEN", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            handleException(response, HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String errorCode, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(status.value(), errorCode, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}