package dev.ilya_anna.auth_service.authorizers;

import dev.ilya_anna.auth_service.security.DaoUserDetails;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Component
public class DaoUserAuthorizer implements AuthorizationManager<RequestAuthorizationContext> {


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext authorizationContext) {;
        
        Authentication authentication = authenticationSupplier.get();
        //get request sender
        DaoUserDetails userDetails = (DaoUserDetails) authentication.getPrincipal();
        //get user id from authorization context
        String userId = authorizationContext.getVariables().get("userId");
        //authorize only if request sender id equals user id
        String requestSenderId = userDetails.getUser().getId();
        return new AuthorizationDecision(userId.equals(requestSenderId));
    }
}
