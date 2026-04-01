package org.ikasan.security.service.authentication;

import org.ikasan.spec.security.service.AuthenticationService;
import org.ikasan.spec.security.service.AuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by majean on 23/12/2017.
 */
public class CustomAuthenticationProvider implements AuthenticationProvider
{
    private AuthenticationService authenticationService;

    public CustomAuthenticationProvider(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
        if(this.authenticationService == null) {
            throw new IllegalArgumentException("authenticationService cannot be null!");
        }
    }

    @Override
    public Authentication authenticate(Authentication auth)
        throws AuthenticationException
    {

        String username = auth.getName();
        String password = auth.getCredentials() == null ? "null" : auth.getCredentials()
            .toString();
        try
        {
            return authenticationService.login(username,password);
        }
        catch (AuthenticationServiceException e)
        {
            throw new
                BadCredentialsException("External system authentication failed",e);

        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        if(auth == null) return false;
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}