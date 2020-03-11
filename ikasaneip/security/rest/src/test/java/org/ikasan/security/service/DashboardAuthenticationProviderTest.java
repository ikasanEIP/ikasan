package org.ikasan.security.service;

import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.authentication.DashboardAuthenticationProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;

public class DashboardAuthenticationProviderTest
{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DashboardAuthenticationProvider uut;

    private DashboardUserServiceImpl dashboardUserService = Mockito.mock(DashboardUserServiceImpl.class);

    @Before
    public void setup()
    {
        uut = new DashboardAuthenticationProvider(dashboardUserService);
    }

    @Test
    public void authenticate_successful()
    {
        User user = setupUser("testUser");
        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(true);
        Mockito.when(dashboardUserService.loadUserByUsername("admin")).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertEquals(true, result.isAuthenticated());
        assertEquals(25, result.getAuthorities().size());
        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verify(dashboardUserService).loadUserByUsername("admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
    }

    @Test
    public void authenticate_successful_and_loadUserByUsername_throws_excption()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("User not found: admin");

        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(true);
        Mockito.when(dashboardUserService.loadUserByUsername("admin")).thenThrow(new UsernameNotFoundException("User not found: admin"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verify(dashboardUserService).loadUserByUsername("admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
    }

    @Test
    public void authenticate_failed()
    {

        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertEquals(false, result.isAuthenticated());
        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
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
