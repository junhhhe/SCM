package FBI.scm.jwt;

import FBI.scm.dto.CustomUserDetails;
import FBI.scm.dto.MemberDto;
import FBI.scm.entity.MemberEntity;
import FBI.scm.enums.MemberRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 Authorization 값 추출
        String authorization = request.getHeader(AUTHORIZATION);

        // 헤더가 없거나 Bearer 토큰이 아닌 경우 다음 필터로 전달
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 실제 토큰 값 추출
        String token = authorization.split(" ")[1];

        try {
            // 토큰이 비어있거나 만료된 경우 다음 필터로 전달
            if (StringUtils.isBlank(token) || jwtUtil.isExpired(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰의 카테고리가 accessToken인지 확인
            String category = jwtUtil.getCategory(token);
            if (!category.equals(ACCESS_TOKEN)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 사용자 정보 추출
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            MemberDto memberDto = new MemberDto(username, role);

            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsername(memberDto.getUsername());
            memberEntity.setRole(MemberRole.valueOf(memberDto.getRole()));

            CustomUserDetails customUserDetails = new CustomUserDetails(memberEntity);

            // 스프링 시큐리티 인증 토큰 생성 및 SecurityContext에 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 경우에도 다음 필터로 전달
            filterChain.doFilter(request, response);
            return;

        } catch (Exception e) {
            // 예기치 않은 예외 처리
            e.printStackTrace();
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}