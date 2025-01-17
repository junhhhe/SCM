package FBI.scm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    int errorType;
    String errorMessage;
    String additionalInfo;
}
