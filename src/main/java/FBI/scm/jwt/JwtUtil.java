package FBI.scm.jwt;

import FBI.scm.dto.ReissueDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.entity.RefreshToken;
import FBI.scm.handler.UnAuthorizedException;
import FBI.scm.repository.MemberRepository;
import FBI.scm.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    // 비밀키 값을 SecretKey 객체로 반환
    public JwtUtil(@Value("${spring.jwt.secret}") String key, RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    // 토큰 생성
    public String createJwt(String category, String username, String role, int expiredMinute){
        // iat, exp를 위한 Date 및 Calendar
        Calendar expCalendar = Calendar.getInstance();
        expCalendar.add(Calendar.MINUTE, Math.toIntExact(expiredMinute));
        Date iatDate = new Date();
        Date expDate = expCalendar.getTime();

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(iatDate)
                .expiration(expDate)
                .signWith(secretKey)
                .compact();
    }


    //Access Token 생성
    public String createAccessToken(String username, String role) {
        return createJwt(ACCESS_TOKEN, username, role, 15); // 15분 만료
    }


    //Refresh Token 생성
    public String createRefreshToken(String username, String role) {
        return createJwt(REFRESH_TOKEN, username, role, 1440 * 7); // 7일 만료
    }

    // 토큰 검증 - 카테고리
    public String getCategory(String token){
        Jws<Claims> jws =  Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return jws.getPayload().get("category", String.class);
    }

    // 토큰 검증 - 아이디
    public String getUsername(String token){
        Jws<Claims> jws =  Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return jws.getPayload().get("username", String.class);
    }

    // 토큰 검증 - role
    public String getRole(String token){
        Jws<Claims> jws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return jws.getPayload().get("role", String.class);
    }

    // 토큰 검증 - 토큰 유효기간 비교
    public Boolean isExpired(String token){

        try{
            Jws<Claims> jws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            Date expDate = jws.getPayload().getExpiration();
            // 현재 날짜가 exp 날짜보다 뒤에 있으면, 만료됨
            return new Date().after(expDate);

        } catch (ExpiredJwtException e){
            e.printStackTrace();
            return true;
        }

    }

    @Transactional
    public void saveRefreshToken(RefreshToken refreshToken) {
        // 기존 Refresh Token 삭제 (중복 방지)
        refreshTokenRepository.deleteByUsername(refreshToken.getUsername());

        // 유효 기간 설정 : 7일
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(refreshToken.isRememberMe() ? 30 : 7);
        refreshToken.setExpiresAt(expiresAt);

        // 새 Refresh Token 저장
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenDto reissueToken(ReissueDto dto) {
        try {
            // 리프레시 토큰 카테고리 검증
            String category = getCategory(dto.getRefreshToken());
            if (!REFRESH_TOKEN.equals(category)) {
                throw new IllegalArgumentException("refreshToken 이 아닙니다.");
            }

            // 사용자 ID 추출
            String username = getUsername(dto.getRefreshToken());

            //리프레시 토큰 조회
            RefreshToken storedRefreshToken = refreshTokenRepository.findByUsername(username)
                    .orElseThrow(() -> new UnAuthorizedException("유효하지 않은 Refresh Token입니다."));

            //DB 저장된 리프레시 토큰과 요청된 리프레시 토큰 비교
            if (!storedRefreshToken.getRefreshToken().equals(dto.getRefreshToken())) {
                // 다른 환경에서 로그인한 경우
                refreshTokenRepository.deleteById(storedRefreshToken.getId());
                throw new UnAuthorizedException("다른 환경에서 로그인한 이력이 있어 재인증이 필요합니다.");
            }

            // 새로운 액세스 토큰 및 리프레시 토큰 생성
            String newAccessToken = createAccessToken(username, "USER_ROLE");
            String newRefreshToken = createRefreshToken(username, "USER_ROLE");

            // 새로운 Refresh Token 저장
            storedRefreshToken.setRefreshToken(newRefreshToken);
            refreshTokenRepository.save(storedRefreshToken);

            return new TokenDto(newAccessToken, newRefreshToken);

        } catch (JwtException e) {
            throw new UnAuthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}
