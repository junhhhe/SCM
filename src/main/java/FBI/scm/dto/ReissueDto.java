package FBI.scm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//RefreshToken 재발급 Dto
public class ReissueDto {
    private String refreshToken;
}
