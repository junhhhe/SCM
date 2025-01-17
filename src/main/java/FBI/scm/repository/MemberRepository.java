package FBI.scm.repository;

import FBI.scm.entity.MemberEntity;
import FBI.scm.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    MemberEntity findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<MemberEntity> findByUsernameAndStatus(String username, MemberStatus status);
}
