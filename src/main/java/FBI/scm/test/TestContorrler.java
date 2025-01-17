package FBI.scm.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "api test", description = "테스트 API Document")
public class TestContorrler {

    @GetMapping("/test")
    @Operation(summary = "test", description = "테스트 API 입니다.")
    public String test(){
        return "Test Complete!!";
    }
}
