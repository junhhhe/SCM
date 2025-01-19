package FBI.scm.repository;

import FBI.scm.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    MemberEntity findByUsername(String username);

    boolean existsByUsername(String username);
}
