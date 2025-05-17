package dev.ilya_anna.auth_service.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.exceptions.UserNotFoundException;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultRefreshService implements RefreshService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Refreshes JWT tokens using the provided refresh token.
     *
     * This method validates the provided refresh token, retrieves the associated
     * user, and generates new access and refresh tokens for the user.
     *
     * @param refreshToken the refresh token used to authenticate and refresh the user's session
     * @return a JwtDto containing new access and refresh tokens
     * @throws UserNotFoundException if the user associated with the refresh token is not found
     * @throws JWTVerificationException if the refresh token is invalid
     */

    @Override
    public JwtDto refresh(String refreshToken) {

        //validate refresh token
        DecodedJWT decodedJwt = jwtService.verifyRefreshToken(refreshToken);

        //retrieve data
        String userId = decodedJwt.getClaim("userId").asString();

        //get user for new tokens
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId + " not found")
        );

        //generate new tokens
        return new JwtDto(jwtService.generateRefresh(user), jwtService.generateAccess(user));
    }
}
