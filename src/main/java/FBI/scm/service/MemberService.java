package FBI.scm.service;

import FBI.scm.dto.CustomUserDetails;
import FBI.scm.dto.JoinDto;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.entity.MemberEntity;
import FBI.scm.entity.RefreshToken;
import FBI.scm.enums.MemberRole;
import FBI.scm.handler.exception.InvalidTokenException;
import FBI.scm.handler.exception.JoinException;
import FBI.scm.handler.exception.LoginException;
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

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void join(JoinDto joinDto){

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

    public TokenDto login(LoginDto loginDto) {
        try {
            // 사용자 입력값을 기반으로 인증 토큰 생성
            String username = loginDto.getUsername();
            String password = loginDto.getPassword();
            Boolean rememberMe = loginDto.getRememberMe();

            // Username과 Password 기반으로 인증 토큰 생성 및 인증 시도
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 인증 성공 후 액세스 토큰과 리프레시 토큰 생성
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(username, role);
            String refreshToken = jwtUtil.createRefreshToken(username, role);

            // RefreshToken 저장
            RefreshToken refreshTokenEntity = new RefreshToken(username, refreshToken, rememberMe);
            jwtUtil.saveRefreshToken(refreshTokenEntity);

            // 토큰 DTO 반환
            return new TokenDto(accessToken, refreshToken);
        } catch(Exception e){
            throw new LoginException("로그인 실패. 아이디 또는 비밀번호를 확인하세요.");
        }
    }

    public void logout(String refresh) {
        if (refresh == null || refresh.trim().isEmpty()) {
            throw new InvalidTokenException("refreshToken이 비어있습니다");
        }

        try {
            String category = jwtUtil.getCategory(refresh);
            if (!REFRESH_TOKEN.equals(category)) {
                throw new InvalidTokenException("refreshToken 이 아닙니다.");
            }

            String username = jwtUtil.getUsername(refresh);
            refreshTokenRepository.deleteByUsername(username);

        } catch (JwtException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidTokenException("로그아웃 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
