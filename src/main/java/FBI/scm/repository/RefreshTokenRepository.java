package FBI.scm.repository;

import FBI.scm.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUsername(String username);

    void deleteByUsername(String username);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens(); // 만료된 토큰 삭제
}