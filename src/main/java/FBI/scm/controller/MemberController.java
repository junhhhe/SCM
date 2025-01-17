package FBI.scm.controller;

import FBI.scm.dto.JoinDto;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.dto.response.BasicResponse;
import FBI.scm.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 회원",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponse.class)))
    })
    public ResponseEntity<BasicResponse<String>> join(@RequestBody JoinDto joinDto) {

        memberService.joinService(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BasicResponse<>(true, "회원가입 성공", null));
    }

    @PostMapping("/login")
    public ResponseEntity<BasicResponse<TokenDto>> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        TokenDto tokenDto = memberService.loginService(loginDto, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BasicResponse<>(true, "로그인 성공", tokenDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<BasicResponse<String>> logout(@RequestHeader("REFRESH_TOKEN") String refreshToken) {
        try {
            memberService.logoutService(refreshToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BasicResponse<>(true, "로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BasicResponse<>(false, "로그아웃 실패", null));
        }
    }
}
