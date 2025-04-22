package dev.ilya_anna.auth_service.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.ilya_anna.auth_service.entities.Role;
import dev.ilya_anna.auth_service.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
@Service
public class DefaultJwtService implements JwtService{

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.subject}")
    private String subject;

    @Value("${app.jwt.access.duration}")
    private Long accessDuration;

    @Value("${app.jwt.refresh.duration}")
    private Long refreshDuration;

    @Value("${app.jwt.access.secret}")
    private String accessSecret;

    @Value("${app.jwt.refresh.secret}")
    private String refreshSecret;

    @Override
    public String generateRefresh(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withExpiresAt(ZonedDateTime.now().plusDays(refreshDuration).toInstant())
                .withClaim("userId", user.getId())
                .sign(Algorithm.HMAC256(refreshSecret));
    }

    @Override
    public String generateAccess(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withExpiresAt(ZonedDateTime.now().plusDays(accessDuration).toInstant())
                .withClaim("userId", user.getId())
                .withClaim("roles", user.getRoles().stream().map(Role::getName).toList())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC256(accessSecret));
    }

    @Override
    public DecodedJWT verifyAccessToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(accessSecret))
                .withSubject(subject)
                .withIssuer(issuer)
                .withClaimPresence("userId")
                .withClaimPresence("roles")
                .withClaimPresence("username")
                .build();

        return verifier.verify(token);
    }

    @Override
    public DecodedJWT verifyRefreshToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(accessSecret))
                .withSubject(subject)
                .withIssuer(issuer)
                .withClaimPresence("userId")
                .build();

        return verifier.verify(token);
    }
}
