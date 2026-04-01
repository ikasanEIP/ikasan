package org.ikasan.security.service;

import org.ikasan.spec.security.dao.SecurityDao;
import org.ikasan.spec.security.model.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for SecurityServiceImpl
 *
 * @author Ikasan Development Team
 */
public class SecurityServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private SecurityDao securityDao = mockery.mock(SecurityDao.class);
    private IkasanPrincipal principal = mockery.mock(IkasanPrincipal.class);
    private Role role = mockery.mock(Role.class);
    private Policy policy = mockery.mock(Policy.class);
    private RoleModule roleModule = mockery.mock(RoleModule.class);
    private RoleJobPlan roleJobPlan = mockery.mock(RoleJobPlan.class);
    private AuthenticationMethod authenticationMethod = mockery.mock(AuthenticationMethod.class);
    private IkasanPrincipalFilter principalFilter = mockery.mock(IkasanPrincipalFilter.class);
    private IkasanPrincipalLite principalLite = mockery.mock(IkasanPrincipalLite.class);
    private User user = mockery.mock(User.class);

    private SecurityServiceImpl securityService;

    @Before
    public void setup()
    {
        securityService = new SecurityServiceImpl(securityDao);
    }

    /**
     * Test constructor with null securityDao throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_securityDao()
    {
        new SecurityServiceImpl(null);
    }

    /**
     * Test constructor with valid securityDao
     */
    @Test
    public void test_constructor_success()
    {
        SecurityServiceImpl service = new SecurityServiceImpl(securityDao);
        assertNotNull(service);
    }

    /**
     * Test createPrincipal delegates to DAO
     */
    @Test
    public void test_createPrincipal()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createPrincipal();
            will(returnValue(principal));
        }});

        IkasanPrincipal result = securityService.createPrincipal();

        assertEquals(principal, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test createNewPrincipal creates and saves principal
     */
    @Test
    public void test_createNewPrincipal()
    {
        final String name = "testPrincipal";
        final String type = "user";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createPrincipal();
            will(returnValue(principal));

            oneOf(principal).setName(name);
            oneOf(principal).setType(type);
            oneOf(principal).setDescription("description");

            oneOf(securityDao).saveOrUpdatePrincipal(principal);
        }});

        IkasanPrincipal result = securityService.createNewPrincipal(name, type);

        assertEquals(principal, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test savePrincipal delegates to DAO
     */
    @Test
    public void test_savePrincipal()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveOrUpdatePrincipal(principal);
        }});

        securityService.savePrincipal(principal);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deletePrincipal delegates to DAO
     */
    @Test
    public void test_deletePrincipal()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deletePrincipal(principal);
        }});

        securityService.deletePrincipal(principal);

        mockery.assertIsSatisfied();
    }

    /**
     * Test findPrincipalByName delegates to DAO
     */
    @Test
    public void test_findPrincipalByName()
    {
        final String name = "testPrincipal";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalByName(name);
            will(returnValue(principal));
        }});

        IkasanPrincipal result = securityService.findPrincipalByName(name);

        assertEquals(principal, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipals delegates to DAO
     */
    @Test
    public void test_getAllPrincipals()
    {
        final List<IkasanPrincipal> principals = new ArrayList<>();
        principals.add(principal);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipals();
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getAllPrincipals();

        assertEquals(principals, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipalLites delegates to DAO
     */
    @Test
    public void test_getAllPrincipalLites()
    {
        final List<IkasanPrincipalLite> principalLites = new ArrayList<>();
        principalLites.add(principalLite);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipalLites();
            will(returnValue(principalLites));
        }});

        List<IkasanPrincipalLite> result = securityService.getAllPrincipalLites();

        assertEquals(principalLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalCount delegates to DAO
     */
    @Test
    public void test_getPrincipalCount()
    {
        final int expectedCount = 10;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalCount(principalFilter);
            will(returnValue(expectedCount));
        }});

        int result = securityService.getPrincipalCount(principalFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalsWithRoleCount delegates to DAO
     */
    @Test
    public void test_getPrincipalsWithRoleCount()
    {
        final String roleName = "Admin";
        final int expectedCount = 5;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalsWithRoleCount(roleName, principalFilter);
            will(returnValue(expectedCount));
        }});

        int result = securityService.getPrincipalsWithRoleCount(roleName, principalFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test createRole delegates to DAO
     */
    @Test
    public void test_createRole()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createRole();
            will(returnValue(role));
        }});

        Role result = securityService.createRole();

        assertEquals(role, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test createNewRole creates and saves role
     */
    @Test
    public void test_createNewRole()
    {
        final String name = "Admin";
        final String description = "Administrator role";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createRole();
            will(returnValue(role));

            oneOf(role).setName(name);
            oneOf(role).setDescription(description);

            oneOf(securityDao).saveOrUpdateRole(role);
        }});

        Role result = securityService.createNewRole(name, description);

        assertEquals(role, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test saveRole delegates to DAO
     */
    @Test
    public void test_saveRole()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveOrUpdateRole(role);
        }});

        securityService.saveRole(role);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deleteRole delegates to DAO
     */
    @Test
    public void test_deleteRole()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deleteRole(role);
        }});

        securityService.deleteRole(role);

        mockery.assertIsSatisfied();
    }

    /**
     * Test findRoleByName delegates to DAO
     */
    @Test
    public void test_findRoleByName()
    {
        final String name = "Admin";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleByName(name);
            will(returnValue(role));
        }});

        Role result = securityService.findRoleByName(name);

        assertEquals(role, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllRoles delegates to DAO
     */
    @Test
    public void test_getAllRoles()
    {
        final List<Role> roles = new ArrayList<>();
        roles.add(role);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllRoles();
            will(returnValue(roles));
        }});

        List<Role> result = securityService.getAllRoles();

        assertEquals(roles, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test createPolicy delegates to DAO
     */
    @Test
    public void test_createPolicy()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createPolicy();
            will(returnValue(policy));
        }});

        Policy result = securityService.createPolicy();

        assertEquals(policy, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test createNewPolicy creates and saves policy
     */
    @Test
    public void test_createNewPolicy()
    {
        final String name = "ReadPolicy";
        final String description = "Read access policy";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createPolicy();
            will(returnValue(policy));

            oneOf(policy).setName(name);
            oneOf(policy).setDescription(description);

            oneOf(securityDao).saveOrUpdatePolicy(policy);
        }});

        Policy result = securityService.createNewPolicy(name, description);

        assertEquals(policy, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test savePolicy delegates to DAO
     */
    @Test
    public void test_savePolicy()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveOrUpdatePolicy(policy);
        }});

        securityService.savePolicy(policy);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deletePolicy delegates to DAO
     */
    @Test
    public void test_deletePolicy()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deletePolicy(policy);
        }});

        securityService.deletePolicy(policy);

        mockery.assertIsSatisfied();
    }

    /**
     * Test findPolicyByName delegates to DAO
     */
    @Test
    public void test_findPolicyByName()
    {
        final String name = "ReadPolicy";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPolicyByName(name);
            will(returnValue(policy));
        }});

        Policy result = securityService.findPolicyByName(name);

        assertEquals(policy, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPolicies delegates to DAO
     */
    @Test
    public void test_getAllPolicies()
    {
        final List<Policy> policies = new ArrayList<>();
        policies.add(policy);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPolicies();
            will(returnValue(policies));
        }});

        List<Policy> result = securityService.getAllPolicies();

        assertEquals(policies, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test createRoleModule delegates to DAO
     */
    @Test
    public void test_createRoleModule()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createRoleModule();
            will(returnValue(roleModule));
        }});

        RoleModule result = securityService.createRoleModule();

        assertEquals(roleModule, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test saveRoleModule delegates to DAO
     */
    @Test
    public void test_saveRoleModule()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveRoleModule(roleModule);
        }});

        securityService.saveRoleModule(roleModule);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deleteRoleModule delegates to DAO
     */
    @Test
    public void test_deleteRoleModule()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deleteRoleModule(roleModule);
        }});

        securityService.deleteRoleModule(roleModule);

        mockery.assertIsSatisfied();
    }

    /**
     * Test createRoleJobPlan delegates to DAO
     */
    @Test
    public void test_createRoleJobPlan()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createRoleJobPlan();
            will(returnValue(roleJobPlan));
        }});

        RoleJobPlan result = securityService.createRoleJobPlan();

        assertEquals(roleJobPlan, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test saveRoleJobPlan delegates to DAO
     */
    @Test
    public void test_saveRoleJobPlan()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveRoleJobPlan(roleJobPlan);
        }});

        securityService.saveRoleJobPlan(roleJobPlan);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deleteRoleJobPlan delegates to DAO
     */
    @Test
    public void test_deleteRoleJobPlan()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deleteRoleJobPlan(roleJobPlan);
        }});

        securityService.deleteRoleJobPlan(roleJobPlan);

        mockery.assertIsSatisfied();
    }

    /**
     * Test createAuthenticationMethod delegates to DAO
     */
    @Test
    public void test_createAuthenticationMethod()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).createAuthenticationMethod();
            will(returnValue(authenticationMethod));
        }});

        AuthenticationMethod result = securityService.createAuthenticationMethod();

        assertEquals(authenticationMethod, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test saveOrUpdateAuthenticationMethod delegates to DAO
     */
    @Test
    public void test_saveOrUpdateAuthenticationMethod()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).saveOrUpdateAuthenticationMethod(authenticationMethod);
        }});

        securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);

        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethods delegates to DAO
     */
    @Test
    public void test_getAuthenticationMethods()
    {
        final List<AuthenticationMethod> methods = new ArrayList<>();
        methods.add(authenticationMethod);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethods();
            will(returnValue(methods));
        }});

        List<AuthenticationMethod> result = securityService.getAuthenticationMethods();

        assertEquals(methods, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethod delegates to DAO
     */
    @Test
    public void test_getAuthenticationMethod()
    {
        final Object id = 1L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethod(id);
            will(returnValue(authenticationMethod));
        }});

        AuthenticationMethod result = securityService.getAuthenticationMethod(id);

        assertEquals(authenticationMethod, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test deleteAuthenticationMethod delegates to DAO
     */
    @Test
    public void test_deleteAuthenticationMethod()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).deleteAuthenticationMethod(authenticationMethod);
        }});

        securityService.deleteAuthenticationMethod(authenticationMethod);

        mockery.assertIsSatisfied();
    }

    /**
     * Test getNumberOfAuthenticationMethods delegates to DAO
     */
    @Test
    public void test_getNumberOfAuthenticationMethods()
    {
        final long expectedCount = 3L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getNumberOfAuthenticationMethods();
            will(returnValue(expectedCount));
        }});

        long result = securityService.getNumberOfAuthenticationMethods();

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethodByOrder delegates to DAO
     */
    @Test
    public void test_getAuthenticationMethodByOrder()
    {
        final long order = 1L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethodByOrder(order);
            will(returnValue(authenticationMethod));
        }});

        AuthenticationMethod result = securityService.getAuthenticationMethodByOrder(order);

        assertEquals(authenticationMethod, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersAssociatedWithPrincipal delegates to DAO
     */
    @Test
    public void test_getUsersAssociatedWithPrincipal()
    {
        final Object principalId = 1L;
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getUsersAssociatedWithPrincipal(principalId);
            will(returnValue(users));
        }});

        List<User> result = securityService.getUsersAssociatedWithPrincipal(principalId);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getRoleById delegates to DAO
     */
    @Test
    public void test_getRoleById()
    {
        final Object id = 1L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleById(id);
            will(returnValue(role));
        }});

        Role result = securityService.getRoleById(id);

        assertEquals(role, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPolicyById delegates to DAO
     */
    @Test
    public void test_getPolicyById()
    {
        final Object id = 1L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPolicyById(id);
            will(returnValue(policy));
        }});

        Policy result = securityService.getPolicyById(id);

        assertEquals(policy, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test setJobPlanRoles with existing role job plans
     */
    @Test
    public void test_setJobPlanRoles_with_existing_plans()
    {
        final String jobPlanName = "TestJobPlan";
        final List<String> roleNames = new ArrayList<>();
        roleNames.add("Admin");
        roleNames.add("User");

        final List<RoleJobPlan> existingPlans = new ArrayList<>();
        final RoleJobPlan existingPlan = mockery.mock(RoleJobPlan.class, "existingPlan");
        existingPlans.add(existingPlan);

        final Role existingRole = mockery.mock(Role.class, "existingRole");
        final Set<RoleJobPlan> roleJobPlanSet = new HashSet<>();
        roleJobPlanSet.add(existingPlan);

        final Role adminRole = mockery.mock(Role.class, "adminRole");
        final Role userRole = mockery.mock(Role.class, "userRole");
        final RoleJobPlan newPlan1 = mockery.mock(RoleJobPlan.class, "newPlan1");
        final RoleJobPlan newPlan2 = mockery.mock(RoleJobPlan.class, "newPlan2");

        mockery.checking(new Expectations()
        {{
            // Delete existing plans
            oneOf(securityDao).getRoleJobPlansByJobPlanName(jobPlanName);
            will(returnValue(existingPlans));

            oneOf(existingPlan).getRole();
            will(returnValue(existingRole));

            oneOf(existingRole).getId();
            will(returnValue(1L));

            oneOf(securityDao).getRoleById(1L);
            will(returnValue(existingRole));

            oneOf(existingRole).getRoleJobPlans();
            will(returnValue(roleJobPlanSet));

            oneOf(existingRole).setRoleJobPlans(with(any(Set.class)));

            oneOf(securityDao).saveOrUpdateRole(existingRole);

            oneOf(securityDao).deleteRoleJobPlan(existingPlan);

            // Create new plans
            oneOf(securityDao).getRoleByName("Admin");
            will(returnValue(adminRole));

            oneOf(securityDao).createRoleJobPlan();
            will(returnValue(newPlan1));

            oneOf(newPlan1).setRole(adminRole);
            oneOf(newPlan1).setJobPlanName(jobPlanName);

            oneOf(securityDao).saveRoleJobPlan(newPlan1);

            oneOf(securityDao).getRoleByName("User");
            will(returnValue(userRole));

            oneOf(securityDao).createRoleJobPlan();
            will(returnValue(newPlan2));

            oneOf(newPlan2).setRole(userRole);
            oneOf(newPlan2).setJobPlanName(jobPlanName);

            oneOf(securityDao).saveRoleJobPlan(newPlan2);
        }});

        securityService.setJobPlanRoles(jobPlanName, roleNames);

        mockery.assertIsSatisfied();
    }

    /**
     * Test setJobPlanRoles with non-existent role
     */
    @Test
    public void test_setJobPlanRoles_with_nonexistent_role()
    {
        final String jobPlanName = "TestJobPlan";
        final List<String> roleNames = new ArrayList<>();
        roleNames.add("NonExistentRole");

        final List<RoleJobPlan> existingPlans = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleJobPlansByJobPlanName(jobPlanName);
            will(returnValue(existingPlans));

            oneOf(securityDao).getRoleByName("NonExistentRole");
            will(returnValue(null));
        }});

        securityService.setJobPlanRoles(jobPlanName, roleNames);

        mockery.assertIsSatisfied();
    }

    /**
     * Test setJobPlanRoles with empty role names list
     */
    @Test
    public void test_setJobPlanRoles_empty_roleNames()
    {
        final String jobPlanName = "TestJobPlan";
        final List<String> roleNames = new ArrayList<>();
        final List<RoleJobPlan> existingPlans = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleJobPlansByJobPlanName(jobPlanName);
            will(returnValue(existingPlans));
        }});

        securityService.setJobPlanRoles(jobPlanName, roleNames);

        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipals with filter, limit, and offset
     */
    @Test
    public void test_getPrincipals_with_filter()
    {
        final int limit = 10;
        final int offset = 0;
        final List<IkasanPrincipal> principals = new ArrayList<>();
        principals.add(principal);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipals(principalFilter, limit, offset);
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getPrincipals(principalFilter, limit, offset);

        assertEquals(principals, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalLites with filter, limit, and offset
     */
    @Test
    public void test_getPrincipalLites_with_filter()
    {
        final int limit = 10;
        final int offset = 0;
        final List<IkasanPrincipalLite> principalLites = new ArrayList<>();
        principalLites.add(principalLite);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalLites(principalFilter, limit, offset);
            will(returnValue(principalLites));
        }});

        List<IkasanPrincipalLite> result = securityService.getPrincipalLites(principalFilter, limit, offset);

        assertEquals(principalLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipalsWithRole with role name
     */
    @Test
    public void test_getAllPrincipalsWithRole()
    {
        final String roleName = "Admin";
        final List<IkasanPrincipal> principals = new ArrayList<>();
        principals.add(principal);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipalsWithRole(roleName);
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getAllPrincipalsWithRole(roleName);

        assertEquals(principals, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipalsWithRole with filter, limit, and offset
     */
    @Test
    public void test_getAllPrincipalsWithRole_with_filter()
    {
        final String roleName = "Admin";
        final int limit = 10;
        final int offset = 0;
        final List<IkasanPrincipalLite> principalLites = new ArrayList<>();
        principalLites.add(principalLite);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipalsWithRole(roleName, principalFilter, limit, offset);
            will(returnValue(principalLites));
        }});

        List<IkasanPrincipalLite> result = securityService.getAllPrincipalsWithRole(roleName, principalFilter, limit, offset);

        assertEquals(principalLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipalsWithoutRole delegates to DAO
     */
    @Test
    public void test_getAllPrincipalsWithoutRole()
    {
        final String roleName = "Admin";
        final int limit = 10;
        final int offset = 0;
        final List<IkasanPrincipalLite> principalLites = new ArrayList<>();
        principalLites.add(principalLite);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipalsWithoutRole(roleName, principalFilter, limit, offset);
            will(returnValue(principalLites));
        }});

        List<IkasanPrincipalLite> result = securityService.getAllPrincipalsWithoutRole(roleName, principalFilter, limit, offset);

        assertEquals(principalLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalsWithoutRoleCount delegates to DAO
     */
    @Test
    public void test_getPrincipalsWithoutRoleCount()
    {
        final String roleName = "Admin";
        final int expectedCount = 8;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalsWithoutRoleCount(roleName, principalFilter);
            will(returnValue(expectedCount));
        }});

        int result = securityService.getPrincipalsWithoutRoleCount(roleName, principalFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalsByName delegates to DAO
     */
    @Test
    public void test_getPrincipalsByName()
    {
        final List<String> names = new ArrayList<>();
        names.add("principal1");
        names.add("principal2");

        final List<IkasanPrincipal> principals = new ArrayList<>();
        principals.add(principal);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalsByRoleNames(names);
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getPrincipalsByName(names);

        assertEquals(principals, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalByNameLike delegates to DAO
     */
    @Test
    public void test_getPrincipalByNameLike()
    {
        final String name = "test%";
        final List<IkasanPrincipal> principals = new ArrayList<>();
        principals.add(principal);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalByNameLike(name);
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getPrincipalByNameLike(name);

        assertEquals(principals, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getRoleByNameLike delegates to DAO
     */
    @Test
    public void test_getRoleByNameLike()
    {
        final String name = "Admin%";
        final List<Role> roles = new ArrayList<>();
        roles.add(role);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleByNameLike(name);
            will(returnValue(roles));
        }});

        List<Role> result = securityService.getRoleByNameLike(name);

        assertEquals(roles, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPolicyByNameLike delegates to DAO
     */
    @Test
    public void test_getPolicyByNameLike()
    {
        final String name = "Read%";
        final List<Policy> policies = new ArrayList<>();
        policies.add(policy);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPolicyByNameLike(name);
            will(returnValue(policies));
        }});

        List<Policy> result = securityService.getPolicyByNameLike(name);

        assertEquals(policies, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPoliciesWithRole delegates to DAO
     */
    @Test
    public void test_getAllPoliciesWithRole()
    {
        final String roleName = "Admin";
        final List<Policy> policies = new ArrayList<>();
        policies.add(policy);

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPoliciesWithRole(roleName);
            will(returnValue(policies));
        }});

        List<Policy> result = securityService.getAllPoliciesWithRole(roleName);

        assertEquals(policies, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }
    /**
     * Test findPrincipalByName returns null for non-existent principal
     */
    @Test
    public void test_findPrincipalByName_null()
    {
        final String name = "nonExistentPrincipal";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalByName(name);
            will(returnValue(null));
        }});

        IkasanPrincipal result = securityService.findPrincipalByName(name);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test findRoleByName returns null for non-existent role
     */
    @Test
    public void test_findRoleByName_null()
    {
        final String name = "nonExistentRole";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleByName(name);
            will(returnValue(null));
        }});

        Role result = securityService.findRoleByName(name);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test findPolicyByName returns null for non-existent policy
     */
    @Test
    public void test_findPolicyByName_null()
    {
        final String name = "nonExistentPolicy";

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPolicyByName(name);
            will(returnValue(null));
        }});

        Policy result = securityService.findPolicyByName(name);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPrincipals returns empty list
     */
    @Test
    public void test_getAllPrincipals_empty()
    {
        final List<IkasanPrincipal> principals = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPrincipals();
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getAllPrincipals();

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllRoles returns empty list
     */
    @Test
    public void test_getAllRoles_empty()
    {
        final List<Role> roles = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllRoles();
            will(returnValue(roles));
        }});

        List<Role> result = securityService.getAllRoles();

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAllPolicies returns empty list
     */
    @Test
    public void test_getAllPolicies_empty()
    {
        final List<Policy> policies = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAllPolicies();
            will(returnValue(policies));
        }});

        List<Policy> result = securityService.getAllPolicies();

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethods returns empty list
     */
    @Test
    public void test_getAuthenticationMethods_empty()
    {
        final List<AuthenticationMethod> methods = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethods();
            will(returnValue(methods));
        }});

        List<AuthenticationMethod> result = securityService.getAuthenticationMethods();

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalCount returns zero
     */
    @Test
    public void test_getPrincipalCount_zero()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalCount(principalFilter);
            will(returnValue(0));
        }});

        int result = securityService.getPrincipalCount(principalFilter);

        assertEquals(0, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getNumberOfAuthenticationMethods returns zero
     */
    @Test
    public void test_getNumberOfAuthenticationMethods_zero()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getNumberOfAuthenticationMethods();
            will(returnValue(0L));
        }});

        long result = securityService.getNumberOfAuthenticationMethods();

        assertEquals(0L, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethod returns null for non-existent ID
     */
    @Test
    public void test_getAuthenticationMethod_null()
    {
        final Object id = 999L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethod(id);
            will(returnValue(null));
        }});

        AuthenticationMethod result = securityService.getAuthenticationMethod(id);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthenticationMethodByOrder returns null for non-existent order
     */
    @Test
    public void test_getAuthenticationMethodByOrder_null()
    {
        final long order = 999L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getAuthenticationMethodByOrder(order);
            will(returnValue(null));
        }});

        AuthenticationMethod result = securityService.getAuthenticationMethodByOrder(order);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getRoleById returns null for non-existent ID
     */
    @Test
    public void test_getRoleById_null()
    {
        final Object id = 999L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getRoleById(id);
            will(returnValue(null));
        }});

        Role result = securityService.getRoleById(id);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getPolicyById returns null for non-existent ID
     */
    @Test
    public void test_getPolicyById_null()
    {
        final Object id = 999L;

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPolicyById(id);
            will(returnValue(null));
        }});

        Policy result = securityService.getPolicyById(id);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersAssociatedWithPrincipal returns empty list
     */
    @Test
    public void test_getUsersAssociatedWithPrincipal_empty()
    {
        final Object principalId = 1L;
        final List<User> users = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getUsersAssociatedWithPrincipal(principalId);
            will(returnValue(users));
        }});

        List<User> result = securityService.getUsersAssociatedWithPrincipal(principalId);

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test setJobPlanRoles with multiple existing plans
     */
    @Test
    public void test_setJobPlanRoles_multiple_existing_plans()
    {
        final String jobPlanName = "TestJobPlan";
        final List<String> roleNames = new ArrayList<>();
        roleNames.add("NewRole");

        final List<RoleJobPlan> existingPlans = new ArrayList<>();
        final RoleJobPlan existingPlan1 = mockery.mock(RoleJobPlan.class, "existingPlan1");
        final RoleJobPlan existingPlan2 = mockery.mock(RoleJobPlan.class, "existingPlan2");
        existingPlans.add(existingPlan1);
        existingPlans.add(existingPlan2);

        final Role existingRole1 = mockery.mock(Role.class, "existingRole1");
        final Role existingRole2 = mockery.mock(Role.class, "existingRole2");
        final Set<RoleJobPlan> roleJobPlanSet1 = new HashSet<>();
        roleJobPlanSet1.add(existingPlan1);
        final Set<RoleJobPlan> roleJobPlanSet2 = new HashSet<>();
        roleJobPlanSet2.add(existingPlan2);

        final Role newRole = mockery.mock(Role.class, "newRole");
        final RoleJobPlan newPlan = mockery.mock(RoleJobPlan.class, "newPlan");

        mockery.checking(new Expectations()
        {{
            // Delete existing plans
            oneOf(securityDao).getRoleJobPlansByJobPlanName(jobPlanName);
            will(returnValue(existingPlans));

            // First existing plan
            oneOf(existingPlan1).getRole();
            will(returnValue(existingRole1));

            oneOf(existingRole1).getId();
            will(returnValue(1L));

            oneOf(securityDao).getRoleById(1L);
            will(returnValue(existingRole1));

            oneOf(existingRole1).getRoleJobPlans();
            will(returnValue(roleJobPlanSet1));

            oneOf(existingRole1).setRoleJobPlans(with(any(Set.class)));

            oneOf(securityDao).saveOrUpdateRole(existingRole1);

            oneOf(securityDao).deleteRoleJobPlan(existingPlan1);

            // Second existing plan
            oneOf(existingPlan2).getRole();
            will(returnValue(existingRole2));

            oneOf(existingRole2).getId();
            will(returnValue(2L));

            oneOf(securityDao).getRoleById(2L);
            will(returnValue(existingRole2));

            oneOf(existingRole2).getRoleJobPlans();
            will(returnValue(roleJobPlanSet2));

            oneOf(existingRole2).setRoleJobPlans(with(any(Set.class)));

            oneOf(securityDao).saveOrUpdateRole(existingRole2);

            oneOf(securityDao).deleteRoleJobPlan(existingPlan2);

            // Create new plan
            oneOf(securityDao).getRoleByName("NewRole");
            will(returnValue(newRole));

            oneOf(securityDao).createRoleJobPlan();
            will(returnValue(newPlan));

            oneOf(newPlan).setRole(newRole);
            oneOf(newPlan).setJobPlanName(jobPlanName);

            oneOf(securityDao).saveRoleJobPlan(newPlan);
        }});

        securityService.setJobPlanRoles(jobPlanName, roleNames);

        mockery.assertIsSatisfied();
    }

    /**
     * Test getPrincipalsByName with empty list
     */
    @Test
    public void test_getPrincipalsByName_empty_list()
    {
        final List<String> names = new ArrayList<>();
        final List<IkasanPrincipal> principals = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(securityDao).getPrincipalsByRoleNames(names);
            will(returnValue(principals));
        }});

        List<IkasanPrincipal> result = securityService.getPrincipalsByName(names);

        assertNotNull(result);
        assertEquals(0, result.size());
        mockery.assertIsSatisfied();
    }
}
