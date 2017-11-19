package org.ikasan.solr.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.solr.security.AuthenticationPlugin;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Ikasan Development Team on 13/11/2017.
 */
public class IkasanSolrAuthenticationPlugin extends AuthenticationPlugin
{
    private AuthenticationService authenticationService;

    @Override
    public void init(Map<String, Object> map)
    {

    }

    @Override
    public boolean doAuthenticate(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws Exception
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Basic "))
        {
            return false;
        }

        auth = new String(Base64.decodeBase64(auth.substring("Basic ".length())));
        String[] vals = auth.split(":");
        String username = vals[0];
        String password = vals[1];

        Authentication authentication = null;

        try
        {
            authentication = authenticationService.login(username, password);
        }
        catch(AuthenticationServiceException e)
        {
            return false;
        }

        return true;
    }

    @Override
    public void close() throws IOException
    {

    }
}
