package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignInDto;
import dev.ilya_anna.auth_service.security.DaoUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DaoSignInService implements SignInService {
    @Autowired
    private AuthenticationManager daoAuthenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Authenticates a user using the provided sign-in credentials and returns a JWT.
     * 
     * @param signInDto the sign-in credentials containing username and password
     * @return a JwtDto containing access and refresh tokens for the authenticated user
     * @throws AuthenticationException if the authentication fails
     */
    @Override
    public JwtDto signIn(SignInDto signInDto) {

        //attempt to authenticate the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                signInDto.getUsername(),
                signInDto.getPassword());
        authentication = daoAuthenticationManager.authenticate(authentication);

        //get user which was authenticated
        DaoUserDetails userDetails = (DaoUserDetails) authentication.getPrincipal();
        log.info("User {} signed in", userDetails.getUser().getUsername());
        //generate jwt
        return new JwtDto(jwtService.generateRefresh(userDetails.getUser()),
                jwtService.generateAccess(userDetails.getUser()));
    }
}
