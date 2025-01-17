package FBI.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원가입 Dto")
public class JoinDto {

    @Schema(description = "회원 아이디", example = "qwer")
    String username;
    @Schema(description = "회원 비밀번호", example = "1234")
    String password;
    @Schema(description = "회원 이메일", example = "qwer@naver.com")
    String email;
    @Schema(description = "회원 이름", example = "문효찬")
    String name;
}
