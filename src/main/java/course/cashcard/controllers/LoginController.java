package course.cashcard.controllers;

import course.cashcard.requests.LoginRequest;
import course.cashcard.models.UserModel;
import course.cashcard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;

@RestController
public class LoginController {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository repository;
    @Autowired
    private JwtEncoder jwtEncoder;

    private String mint() {
        return mint(consumer -> {});
    }

    private String mint(Consumer<JwtClaimsSet.Builder> consumer) {
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(86400)) //24h
                .subject("unknown")
                .issuer("http://localhost:9000")
                .audience(Arrays.asList("cashcard-client"))
                .claim("scp", Arrays.asList("cashcard:read", "cashcard:write"));
        consumer.accept(builder);
        JwtEncoderParameters parameters = JwtEncoderParameters.from(builder.build());
        return jwtEncoder.encode(parameters).getTokenValue();
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest login){
        UserModel user = repository.findByUsername(login.username());
        if(user!=null) {
            boolean passwordOk =  encoder.matches(login.password(), user.getPassword());
            if (!passwordOk) {
                throw new RuntimeException("Invalid password for" + login.username());
            }

            return mint((claims) -> claims.subject(user.getUsername()).claim("scp", user.getRoles()));
        }else {
            throw new RuntimeException("Error while trying to login");
        }
    }
}