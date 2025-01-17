package FBI.scm.controller;

import FBI.scm.dto.JoinDto;
import FBI.scm.dto.LoginDto;
import FBI.scm.dto.TokenDto;
import FBI.scm.dto.response.BasicResponse;
import FBI.scm.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

        memberService.join(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BasicResponse<>(true, "회원가입 성공", null));
    }

    @Operation(summary = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<TokenDto>> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = memberService.login(loginDto);
        BasicResponse<TokenDto> response = new BasicResponse<>(true, "로그인 성공", tokenDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse<Void>> logout(
            @Parameter(description = "Refresh Token", required = true)
            @RequestHeader("REFRESH_TOKEN") String refreshToken) {
        memberService.logout(refreshToken);
        BasicResponse<Void> response = new BasicResponse<>(true, "Success", null);
        return ResponseEntity.ok(response);
    }
}
