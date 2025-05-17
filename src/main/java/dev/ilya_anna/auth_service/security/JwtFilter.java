package dev.ilya_anna.auth_service.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.exceptions.SignOutMarkValidationException;
import dev.ilya_anna.auth_service.exceptions.UserNotFoundException;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import dev.ilya_anna.auth_service.services.JwtService;
import dev.ilya_anna.auth_service.services.SignOutService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignOutService signOutService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        //get authorization header
        String authorizationHeader = request.getHeader("Authorization");
        try {
            //authorize user only if authorization header is jwt
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // get jwt
                String token = authorizationHeader.substring(7);

                //validate jwt and retrieve data
                DecodedJWT decodedJWT = jwtService.verifyAccessToken(token);
                String userId = decodedJWT.getClaim("userId").asString();
                LocalDateTime issuedAt = decodedJWT.getIssuedAt().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

                //validate relevance
                signOutService.validateSignOutMark(userId, issuedAt);
                User user = userRepository.findById(userId).orElseThrow(
                        () -> new UserNotFoundException("User with id " + userId + " not found")
                );

                //add user to security context
                UserDetails userDetails = new DaoUserDetails(user);
                Authentication authentication = UsernamePasswordAuthenticationToken
                    .authenticated(userDetails, null, userDetails.getAuthorities());
                
                if (SecurityContextHolder.getContext().getAuthentication() == null ||
                        !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            response.getWriter().write("Invalid authorization JWT");
        } catch (UserNotFoundException e) {
            response.getWriter().write(e.getMessage());
        } catch (SignOutMarkValidationException e) {
            response.getWriter().write(e.getMessage());
        }
    }
}
