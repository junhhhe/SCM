package FBI.scm.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 Dto")
public class LoginDto {

    @Schema(description = "로그인 아이디", example = "qwer")
    String username;
    @Schema(description = "로그인 비밀번호", example = "1234")
    String password;
    @Schema(description = "아이디 저장 유무", example = "true")
    Boolean rememberMe;
}
