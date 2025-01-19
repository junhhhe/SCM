package FBI.scm.entity;

import FBI.scm.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_member")
@Data
public class MemberEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;
}
