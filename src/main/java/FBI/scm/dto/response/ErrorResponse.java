package FBI.scm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int errorType;
    private String errorMessage;
    private String additionalInfo;
}
