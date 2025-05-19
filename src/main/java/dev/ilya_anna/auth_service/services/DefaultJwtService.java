package dev.ilya_anna.auth_service.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.ilya_anna.auth_service.entities.Role;
import dev.ilya_anna.auth_service.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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

    /**
     * Generates a refresh JWT token for the specified user.
     *
     * @param user the user for whom the refresh token is being generated
     * @return a JWT refresh token as a String
     */
    @Override
    public String generateRefresh(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(ZonedDateTime.now().toInstant())
                .withExpiresAt(ZonedDateTime.now().plusSeconds(refreshDuration).toInstant())
                .withClaim("userId", user.getId())
                .sign(Algorithm.HMAC256(refreshSecret));
    }
    
    /**
     * Generates an access JWT token for the specified user.
     *
     * @param user the user for whom the access token is being generated
     * @return a JWT access token as a String
     */
    @Override
    public String generateAccess(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(ZonedDateTime.now().toInstant())
                .withExpiresAt(ZonedDateTime.now().plusSeconds(accessDuration).toInstant())
                .withClaim("userId", user.getId())
                .withClaim("roles", user.getRoles().stream().map(Role::getName).toList())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC256(accessSecret));
    }
    
    /**
     * Verifies the specified access token and returns the decoded JWT.
     *
     * @param token the access token to verify
     * @return the decoded JWT
     * @throws JWTVerificationException if the token is invalid
     */
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

    /**
     * Verifies the specified refresh token and returns the decoded JWT.
     *
     * @param token the refresh token to verify
     * @return the decoded JWT
     * @throws JWTVerificationException if the token is invalid
     */
    @Override
    public DecodedJWT verifyRefreshToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(refreshSecret))
                .withSubject(subject)
                .withIssuer(issuer)
                .withClaimPresence("userId")
                .build();

        return verifier.verify(token);
    }
}
