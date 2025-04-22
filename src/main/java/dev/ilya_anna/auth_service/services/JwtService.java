package dev.ilya_anna.auth_service.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ilya_anna.auth_service.entities.User;

public interface JwtService {
    String generateRefresh(User user);
    String generateAccess(User user);
    DecodedJWT verifyAccessToken(String token) throws JWTVerificationException;
    DecodedJWT verifyRefreshToken(String token) throws JWTVerificationException;
}
