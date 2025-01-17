package FBI.scm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "refreshToken", nullable = false)
    private String refreshToken; // 리프레시 토큰 값

    private boolean rememberMe; // rememberMe 설정 여부

    @Column(name = "expiresAt")
    private LocalDateTime expiresAt;

    public RefreshToken(String username, String refreshToken, boolean rememberMe) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.rememberMe = rememberMe;
    }
}