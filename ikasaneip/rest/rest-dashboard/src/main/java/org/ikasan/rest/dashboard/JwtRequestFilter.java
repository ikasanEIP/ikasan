package org.ikasan.rest.dashboard;

import io.jsonwebtoken.ExpiredJwtException;
import org.ikasan.security.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter
{
    private UserService userService;

    private JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(UserService userService, JwtTokenUtil jwtTokenUtil)
    {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException
    {
        final String requestServletUrl = request.getServletPath();
        if (requestServletUrl.startsWith("/rest"))
        {
            final String requestTokenHeader = request.getHeader("Authorization");
            String jwtToken = null;
            // JWT Token is in the form "Bearer token". Remove Bearer word and get
            // only the Token
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))
            {
                jwtToken = requestTokenHeader.substring(7);
                try
                {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    // Once we get the token validate it.
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
                    {
                        UserDetails userDetails = this.userService.loadUserByUsername(username);
                        // if token is valid configure Spring Security to manually set
                        // authentication
                        if (jwtTokenUtil.validateToken(jwtToken, userDetails))
                        {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                            usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            // After setting the Authentication in the context, we specify
                            // that the current user is authenticated. So it passes the
                            // Spring Security Configurations successfully.
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                }
                catch (IllegalArgumentException e)
                {
                    logger.warn("Unable to get JWT Token by [\" + requestServletUrl + \"]");

                }
                catch (ExpiredJwtException e)
                {
                    logger.warn("JWT Token has expired called by [\" + requestServletUrl + \"]");
                }
            }
            else
            {
                logger.warn(
                    "[Authorization] header does not begin with Bearer String on url [" + requestServletUrl + "]");
            }
        }
        chain.doFilter(request, response);
    }
}
