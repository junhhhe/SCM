package FBI.scm.jwt;

import FBI.scm.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtLogoutFilter extends GenericFilterBean {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshRepository;
    private final RequestMatcher logoutRequestMatcher;

    public JwtLogoutFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.logoutRequestMatcher = new AntPathRequestMatcher("/api/v1/member/logout", "POST");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!logoutRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = request.getHeader(REFRESH_TOKEN);
        if (refresh == null || refresh.trim().isEmpty()) {
            throw new IllegalArgumentException("refreshToken이 비어있습니다");
        }

        try {
            String category = jwtUtil.getCategory(refresh);
            if (!REFRESH_TOKEN.equals(category)) {
                throw new IllegalArgumentException("refreshToken 이 아닙니다.");
            }

            String username = jwtUtil.getUsername(refresh);

            refreshRepository.deleteByUsername(username);

        } catch (JwtException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
