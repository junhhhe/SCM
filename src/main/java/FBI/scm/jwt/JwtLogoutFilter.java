package FBI.scm.jwt;

import FBI.scm.service.MemberService;
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

    private final RequestMatcher logoutRequestMatcher;
    private final MemberService memberService;

    public JwtLogoutFilter(MemberService memberService) {
        this.logoutRequestMatcher = new AntPathRequestMatcher("/api/v1/member/logout", "POST");
        this.memberService = memberService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!logoutRequestMatcher.matches(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String refreshToken = httpRequest.getHeader("REFRESH_TOKEN");
        if (refreshToken != null) {
            memberService.logout(refreshToken);
        }

        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }
}
