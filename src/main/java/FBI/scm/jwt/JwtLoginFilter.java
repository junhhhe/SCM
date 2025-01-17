package FBI.scm.jwt;

import FBI.scm.dto.CustomUserDetails;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.entity.RefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static org.springframework.security.config.Elements.REMEMBER_ME;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;

        // 필터가 처리할 로그인 URL 설정
        setFilterProcessesUrl("/api/v1/member/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

            // 사용자 입력값을 기반으로 인증 토큰 생성
            String username = loginDto.getUsername();
            String password = loginDto.getPassword();

            if (username == null || password == null) {
                throw new IllegalArgumentException("아이디 비밀번호 비어있음");
            }

            // Username과 Password 기반으로 인증 토큰 생성 및 인증 시도
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e){
            throw new IllegalArgumentException("로그인 요청 처리 중 오류 발생", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 후 액세스 토큰과 리프레시 토큰 생성
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        boolean rememberMe = Boolean.parseBoolean(request.getParameter(REMEMBER_ME));

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // RefreshToken 저장
        RefreshToken refreshTokenEntity = new RefreshToken(username, refreshToken, rememberMe);
        jwtUtil.saveRefreshToken(refreshTokenEntity);

        // 응답으로 토큰 반환
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), tokenDto);
    }
}
