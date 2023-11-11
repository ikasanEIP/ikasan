package org.ikasan.security.service;

import org.ikasan.security.model.*;
import org.ikasan.security.service.authentication.DashboardAuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DashboardAuthenticationProviderTest
{

    private DashboardAuthenticationProvider uut;

    private DashboardUserServiceImpl dashboardUserService = Mockito.mock(DashboardUserServiceImpl.class);
    private UserServiceImpl alternateUserService = Mockito.mock(UserServiceImpl.class);
    private User user = Mockito.mock(User.class);

    @BeforeEach
    void setup()
    {
        uut = new DashboardAuthenticationProvider(dashboardUserService, null);
    }

    @Test
    void authenticate_successful()
    {
        User user = setupUser("testUser");
        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(true);
        Mockito.when(dashboardUserService.loadUserByUsername("admin")).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertTrue(result.isAuthenticated());
        assertEquals(45, result.getAuthorities().size());

        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        result.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());

        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verify(dashboardUserService).loadUserByUsername("admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
    }

    @Test
    void authenticate_successful_and_loadUserByUsername_throws_excption()
    {
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {

            Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(true);
            Mockito.when(dashboardUserService.loadUserByUsername("admin")).thenThrow(new UsernameNotFoundException("User not found: admin"));

            Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
            Authentication result = uut.authenticate(authentication);
            Mockito.verify(dashboardUserService).authenticate("admin", "admin");
            Mockito.verify(dashboardUserService).loadUserByUsername("admin");
            Mockito.verifyNoMoreInteractions(dashboardUserService);
        });
        assertTrue(exception.getMessage().contains("User not found: admin"));
    }

    @Test
    void authenticate_failed()
    {

        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenReturn(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertFalse(result.isAuthenticated());
        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
    }

    @Test
    void authenticate_failed_primary_success_alternate()
    {
        uut = new DashboardAuthenticationProvider(dashboardUserService, this.alternateUserService);

        User testUser = this.setupUser("test-user");

        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenThrow(new RuntimeException());
        Mockito.when(alternateUserService.loadUserByUsername("admin")).thenReturn(this.user);
        Mockito.when(this.user.getPassword()).thenReturn("{SHA-1}d033e22ae348aeb5660fc2140aec35850c4da997");
        Mockito.when(this.user.getPrincipals()).thenReturn(testUser.getPrincipals());

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertTrue(result.isAuthenticated());

        assertTrue(result.isAuthenticated());
        assertEquals(45, result.getAuthorities().size());

        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        result.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());

        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
        Mockito.verify(alternateUserService).loadUserByUsername("admin");
        Mockito.verifyNoMoreInteractions(alternateUserService);
    }

    @Test
    void authenticate_failed_primary_fail_alternate()
    {
        uut = new DashboardAuthenticationProvider(dashboardUserService, this.alternateUserService);

        Mockito.when(dashboardUserService.authenticate("admin", "admin")).thenThrow(new RuntimeException());
        Mockito.when(alternateUserService.loadUserByUsername("admin")).thenReturn(this.user);
        Mockito.when(this.user.getPassword()).thenReturn("{SHA-1}d033e22ae348aeb5660fc2140aec35850c4da999");

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "admin");
        Authentication result = uut.authenticate(authentication);
        assertFalse(result.isAuthenticated());
        Mockito.verify(dashboardUserService).authenticate("admin", "admin");
        Mockito.verifyNoMoreInteractions(dashboardUserService);
        Mockito.verify(alternateUserService).loadUserByUsername("admin");
        Mockito.verifyNoMoreInteractions(alternateUserService);
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

                RoleModule roleModule = new RoleModule();
                roleModule.setModuleName("role" + i + "moduleName");
                roleModule.setRole(role);
                role.setRoleModules(Set.of(roleModule));

                RoleJobPlan roleJobPlan = new RoleJobPlan();
                roleJobPlan.setJobPlanName("role" + i + "jobPlanName");
                roleJobPlan.setRole(role);
                role.setRoleJobPlans(Set.of(roleJobPlan));
            }
            principal.addRole(role);
        }
        return principal;
    }

}
