package course.cashcard.configs;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationEntryPoint entryPoint)
            throws Exception {
        http
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/cashcards/**")
                        .hasAuthority("SCOPE_cashcard:read")
                        .requestMatchers("/cashcards/**")
                        .hasAuthority("SCOPE_cashcard:write")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .authenticationEntryPoint(entryPoint)
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("classpath:authz.pub") RSAPublicKey pub,
                          @Value("classpath:authz.pem") RSAPrivateKey pem) {
        RSAKey key = new RSAKey.Builder(pub).privateKey(pem).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("classpath:authz.pub") RSAPublicKey pub) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(pub).build();
        OAuth2TokenValidator<Jwt> defaults = JwtValidators.createDefaultWithIssuer("http://localhost:9000");
        OAuth2TokenValidator<Jwt> audience = new JwtClaimValidator<List<Object>>(JwtClaimNames.AUD,
                (aud) -> !Collections.disjoint(aud, Collections.singleton("cashcard-client")));
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaults, audience));
        return jwtDecoder;
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails hankOwnsNoCards = users
                .username("hank-owns-no-cards")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER")
                .build();
        UserDetails kumar = users
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build();
        return new InMemoryUserDetailsManager(hankOwnsNoCards, kumar);
    }
}
