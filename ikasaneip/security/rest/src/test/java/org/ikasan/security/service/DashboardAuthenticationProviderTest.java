package org.ikasan.security.service;

import org.apache.commons.io.FileUtils;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.authentication.DashboardAuthenticationProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DashboardAuthenticationProviderTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DashboardAuthenticationProvider uut;

    private DashboardUserServiceImpl dashboardUserService = mockery.mock(DashboardUserServiceImpl.class);

    @Before
    public void setup()
    {
        uut = new DashboardAuthenticationProvider(dashboardUserService);
    }

    @Test
    public void authenticate_successful()
    {
        User user = setupUser("testUser");
        this.mockery.checking(new Expectations()
        {
            {
                oneOf(dashboardUserService).authenticate("admin", "admin");
                will(returnValue(true));
                oneOf(dashboardUserService).loadUserByUsername("admin");
                will(returnValue(user));
            }
        });
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertEquals(true, result.isAuthenticated());
        assertEquals(25, result.getAuthorities().size());
        mockery.assertIsSatisfied();
    }

    @Test
    public void authenticate_successful_and_loadUserByUsername_throws_excption()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("User not found: admin");

        this.mockery.checking(new Expectations()
        {
            {
                oneOf(dashboardUserService).authenticate("admin", "admin");
                will(returnValue(true));
                oneOf(dashboardUserService).loadUserByUsername("admin");
                will(throwException(new UsernameNotFoundException("User not found: admin")));
            }
        });
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        mockery.assertIsSatisfied();
    }

    @Test
    public void authenticate_failed()
    {
        this.mockery.checking(new Expectations()
        {
            {
                oneOf(dashboardUserService).authenticate("admin", "admin");
                will(returnValue(false));
            }
        });
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertEquals(false, result.isAuthenticated());
        mockery.assertIsSatisfied();
    }

    private User setupUser(String username)
    {
        IkasanPrincipal userPrinciple = setupIkasanPrincipal("User");
        IkasanPrincipal adminPrinciple = setupIkasanPrincipal("ADMIN");
        User expected = new User(username, null, "test@test.com", true);
        expected.setDepartment("department");
        expected.setFirstName("TestName");
        expected.setSurname(username);
        expected.addPrincipal(userPrinciple);
        expected.addPrincipal(adminPrinciple);
        return expected;
    }

    private IkasanPrincipal setupIkasanPrincipal(String principleName)
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName(principleName);
        principal.setType("type");
        for (int i = 0; i < 5; i++)
        {
            Role role = new Role();
            role.setName("role" + i);
            for (int j = 0; j < 5; j++)
            {
                Policy policy = new Policy();
                policy.setName("policy" + j + i);
                policy.setDescription("description");
                role.addPolicy(policy);
            }
            principal.addRole(role);
        }
        return principal;
    }

}
