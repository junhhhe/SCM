package FBI.scm.controller.jwt;

import FBI.scm.dto.ReissueDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Tag(name = "토큰 재발급", description = "RefreshToken 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/jwt")
//토큰 재발급
public class JwtController {

    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<TokenDto> reissue(@RequestBody ReissueDto reissueDto) {

        TokenDto tokenDto = jwtUtil.reissueToken(reissueDto);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }
}
