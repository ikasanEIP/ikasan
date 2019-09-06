package org.ikasan.rest.dashboard;

import io.jsonwebtoken.ExpiredJwtException;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith (SpringJUnit4ClassRunner.class)
public class JwtRequestFilterTest
{

    private JwtRequestFilter uut;

    @Mock
    UserService userService;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain chain;

    @Mock
    User userDetails;

    @Before
    public void setup(){
        uut = new JwtRequestFilter(userService,jwtTokenUtil);
    }

    @After
    public void reset_mocks() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void test_login_page() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/login");

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(request,chain);

    }

    @Test
    public void test_rest_call() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token");
        when(jwtTokenUtil.getUsernameFromToken("test.token")).thenReturn("testUser");
        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("test.token",userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");
        verify(jwtTokenUtil).getUsernameFromToken("test.token");
        verify(userService).loadUserByUsername("testUser");
        verify(jwtTokenUtil).validateToken("test.token",userDetails);
        verify(userDetails).getAuthorities();

        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

    @Test
    public void test_rest_call_when_validate_token_is_false() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token");
        when(jwtTokenUtil.getUsernameFromToken("test.token")).thenReturn("testUser");
        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("test.token",userDetails)).thenReturn(false);

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");
        verify(jwtTokenUtil).getUsernameFromToken("test.token");
        verify(userService).loadUserByUsername("testUser");
        verify(jwtTokenUtil).validateToken("test.token",userDetails);

        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

    @Test
    public void test_rest_call_when_userService_returns_null() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token");
        when(jwtTokenUtil.getUsernameFromToken("test.token")).thenReturn("testUser");
        when(userService.loadUserByUsername("testUser")).thenReturn(null);

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");
        verify(jwtTokenUtil).getUsernameFromToken("test.token");
        verify(userService).loadUserByUsername("testUser");


        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

    @Test
    public void test_rest_call_when_Authorization_does_not_have_bearer() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Basic test.token");

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");

        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

    @Test
    public void test_rest_call_when_jwt_throws_ExpiredJwtException() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token");
        when(jwtTokenUtil.getUsernameFromToken("test.token")).thenThrow( new ExpiredJwtException(null,null,"Test"));

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");
        verify(jwtTokenUtil).getUsernameFromToken("test.token");
        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

    @Test
    public void test_rest_call_when_jwt_throws_IllegalArgumentException() throws ServletException, IOException
    {

        when(request.getServletPath()).thenReturn("/rest");
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token");
        when(jwtTokenUtil.getUsernameFromToken("test.token")).thenThrow( new IllegalArgumentException("Test"));

        uut.doFilterInternal(request,response,chain);

        verify(request).getServletPath();
        verify(request).getHeader("Authorization");
        verify(jwtTokenUtil).getUsernameFromToken("test.token");
        verify(chain).doFilter(request,response);

        verifyNoMoreInteractions(chain,jwtTokenUtil,userService,userDetails,response);

    }

}
