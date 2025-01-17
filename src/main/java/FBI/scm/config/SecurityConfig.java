package FBI.scm.config;

import FBI.scm.jwt.*;
import FBI.scm.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/api/**", "/graphiql", "/graphql",
            "/swagger-ui/**", "/api-docs", "/swagger-ui.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html"
    };

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/member/join", "/api/v1/member/login").permitAll()
                        .requestMatchers("/api/v1/member/logout", "/api/v1/member/jwt").authenticated()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(new JwtLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtRequestFilter(jwtUtil), JwtLoginFilter.class)
                .addFilterAt(new JwtLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)
                .addFilterBefore(new JwtExceptionHandlingFilter(objectMapper), JwtLogoutFilter.class);

        return http.build();
    }
}