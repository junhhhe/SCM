package FBI.scm.service;

import FBI.scm.dto.CustomUserDetails;
import FBI.scm.entity.MemberEntity;
import FBI.scm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity memberData = memberRepository.findByUsername(username);

        if (memberData != null) {
            return new CustomUserDetails(memberData);
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}