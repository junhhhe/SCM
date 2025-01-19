package FBI.scm.controller;

import FBI.scm.dto.JoinDto;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.dto.response.BasicResponse;
import FBI.scm.handler.exception.InvalidTokenException;
import FBI.scm.handler.exception.JoinException;
import FBI.scm.handler.exception.LoginException;
import FBI.scm.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원 API")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    @Operation(summary = "회원가입 API")
    public ResponseEntity<BasicResponse<String>> join(@RequestBody JoinDto joinDto) {
        memberService.join(joinDto);
        BasicResponse<String> response = new BasicResponse<>(true, "회원가입 성공", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<TokenDto>> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = memberService.login(loginDto);
        BasicResponse<TokenDto> response = new BasicResponse<>(true, "로그인 성공", tokenDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse<Void>> logout(
            @Parameter(description = "Refresh Token", required = true)
            @RequestHeader("REFRESH_TOKEN") String refreshToken) {
        memberService.logout(refreshToken);
        BasicResponse<Void> response = new BasicResponse<>(true, "로그아웃 성공", null);
        return ResponseEntity.ok().body(response);
    }
}
