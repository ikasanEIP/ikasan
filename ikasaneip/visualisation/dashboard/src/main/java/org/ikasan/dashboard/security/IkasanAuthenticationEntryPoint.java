package org.ikasan.dashboard.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IkasanAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException)
            throws IOException, ServletException {
        String context = request.getRequestURI();
        if(context.startsWith("/")) {
            context = context.substring(1);
        }

        ContextCache.addContext(request.getSession().getId(), context);
        response.sendRedirect("/");
    }

}