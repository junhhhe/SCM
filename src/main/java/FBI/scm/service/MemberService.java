package FBI.scm.service;

import FBI.scm.dto.CustomUserDetails;
import FBI.scm.dto.JoinDto;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.entity.MemberEntity;
import FBI.scm.entity.RefreshToken;
import FBI.scm.enums.MemberRole;
import FBI.scm.handler.exception.JoinException;
import FBI.scm.handler.exception.LoginException;
import FBI.scm.jwt.JwtLoginFilter;
import FBI.scm.jwt.JwtUtil;
import FBI.scm.repository.MemberRepository;
import FBI.scm.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import static org.springframework.security.config.Elements.REMEMBER_ME;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void joinService(JoinDto joinDto){

        if(memberRepository.existsByUsername(joinDto.getUsername())){
            throw new JoinException("이미 존재하는 사용자입니다.");
        }

        if (joinDto.getPassword() == null || joinDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력하세요.");
        }

        MemberEntity memberEntity= new MemberEntity();
        memberEntity.setUsername(joinDto.getUsername());
        memberEntity.setEmail(joinDto.getEmail());
        memberEntity.setName(joinDto.getName());
        memberEntity.setRole(MemberRole.USER);
        memberEntity.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));

        memberRepository.save(memberEntity);
    }

    // 로그인 서비스
    public TokenDto loginService(LoginDto loginDto) {
        // 로그인 시도
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // AuthenticationManager로 인증 처리
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증이 성공하면 사용자 정보를 기반으로 토큰 생성
        String username = loginDto.getUsername();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // 토큰 반환
        return new TokenDto(accessToken, refreshToken);
    }

    public void logoutService(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("refeshToken이 비어있습니다.");
        }

        try {
            // refreshToken 유효성 확인
            String category = jwtUtil.getCategory(refreshToken);
            if (!"REFRESH_TOKEN".equals(category)) {
                throw new IllegalArgumentException("refreshToken이 아닙니다.");
            }

            // refreshToken에서 username 추출
            String username = jwtUtil.getUsername(refreshToken);

            // DB에서 해당 username의 refreshToken 삭제
            refreshTokenRepository.deleteByUsername(username);

        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("로그아웃 실패: " + e.getMessage());
        }
    }
}
