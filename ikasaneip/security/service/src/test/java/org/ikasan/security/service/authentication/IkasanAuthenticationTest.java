package org.ikasan.security.service.authentication;

import org.ikasan.spec.security.model.Policy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for IkasanAuthentication
 *
 * @author Ikasan Development Team
 */
public class IkasanAuthenticationTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private Principal principal = mockery.mock(Principal.class);
    private GrantedAuthority authority1 = mockery.mock(GrantedAuthority.class, "authority1");
    private GrantedAuthority authority2 = mockery.mock(GrantedAuthority.class, "authority2");
    private Policy policy1 = mockery.mock(Policy.class, "policy1");
    private Policy policy2 = mockery.mock(Policy.class, "policy2");

    private List<GrantedAuthority> authorities;
    private String credentials = "testCredentials";
    private long previousLoginTimestamp = System.currentTimeMillis();

    @Before
    public void setup()
    {
        authorities = new ArrayList<>();
        authorities.add(authority1);
        authorities.add(authority2);
    }

    /**
     * Test constructor with all parameters
     */
    @Test
    public void test_constructor_success()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
    }

    /**
     * Test constructor with false authentication
     */
    @Test
    public void test_constructor_not_authenticated()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(false, principal, authorities, credentials, previousLoginTimestamp);
        assertNotNull(authentication);
        assertFalse(authentication.isAuthenticated());
    }

    /**
     * Test getName returns principal name
     */
    @Test
    public void test_getName()
    {
        final String principalName = "testUser";

        mockery.checking(new Expectations()
        {{
            oneOf(principal).getName();
            will(returnValue(principalName));
        }});

        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertEquals(principalName, authentication.getName());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getAuthorities returns correct authorities
     */
    @Test
    public void test_getAuthorities()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        Collection<? extends GrantedAuthority> result = authentication.getAuthorities();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(authority1));
        assertTrue(result.contains(authority2));
    }

    /**
     * Test getAuthorities with empty list
     */
    @Test
    public void test_getAuthorities_empty()
    {
        List<GrantedAuthority> emptyAuthorities = new ArrayList<>();
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, emptyAuthorities, credentials, previousLoginTimestamp);
        Collection<? extends GrantedAuthority> result = authentication.getAuthorities();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Test getCredentials returns correct credentials
     */
    @Test
    public void test_getCredentials()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertEquals(credentials, authentication.getCredentials());
    }

    /**
     * Test getDetails returns null
     */
    @Test
    public void test_getDetails()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertNull(authentication.getDetails());
    }

    /**
     * Test getPrincipal returns correct principal
     */
    @Test
    public void test_getPrincipal()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertEquals(principal, authentication.getPrincipal());
    }

    /**
     * Test isAuthenticated returns true
     */
    @Test
    public void test_isAuthenticated_true()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertTrue(authentication.isAuthenticated());
    }

    /**
     * Test isAuthenticated returns false
     */
    @Test
    public void test_isAuthenticated_false()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(false, principal, authorities, credentials, previousLoginTimestamp);
        assertFalse(authentication.isAuthenticated());
    }

    /**
     * Test setAuthenticated changes authentication status
     */
    @Test
    public void test_setAuthenticated()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertTrue(authentication.isAuthenticated());

        authentication.setAuthenticated(false);
        assertFalse(authentication.isAuthenticated());

        authentication.setAuthenticated(true);
        assertTrue(authentication.isAuthenticated());
    }

    /**
     * Test getPreviousLoginTimestamp returns correct timestamp
     */
    @Test
    public void test_getPreviousLoginTimestamp()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertEquals(previousLoginTimestamp, authentication.getPreviousLoginTimestamp());
    }

    /**
     * Test setPreviousLoginTimestamp changes timestamp
     */
    @Test
    public void test_setPreviousLoginTimestamp()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertEquals(previousLoginTimestamp, authentication.getPreviousLoginTimestamp());

        long newTimestamp = System.currentTimeMillis() + 1000;
        authentication.setPreviousLoginTimestamp(newTimestamp);
        assertEquals(newTimestamp, authentication.getPreviousLoginTimestamp());
    }

    /**
     * Test hasGrantedAuthority returns true when authority exists
     */
    @Test
    public void test_hasGrantedAuthority_exists()
    {
        final String authorityName = "READ_PRIVILEGE";

        mockery.checking(new Expectations()
        {{
            oneOf(authority1).getAuthority();
            will(returnValue(authorityName));
        }});

        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertTrue(authentication.hasGrantedAuthority(authorityName));
        mockery.assertIsSatisfied();
    }

    /**
     * Test hasGrantedAuthority returns false when authority does not exist
     */
    @Test
    public void test_hasGrantedAuthority_not_exists()
    {
        final String searchAuthority = "WRITE_PRIVILEGE";

        mockery.checking(new Expectations()
        {{
            oneOf(authority1).getAuthority();
            will(returnValue("READ_PRIVILEGE"));

            oneOf(authority2).getAuthority();
            will(returnValue("DELETE_PRIVILEGE"));
        }});

        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertFalse(authentication.hasGrantedAuthority(searchAuthority));
        mockery.assertIsSatisfied();
    }

    /**
     * Test hasGrantedAuthority with empty authorities
     */
    @Test
    public void test_hasGrantedAuthority_empty_authorities()
    {
        List<GrantedAuthority> emptyAuthorities = new ArrayList<>();
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, emptyAuthorities, credentials, previousLoginTimestamp);
        assertFalse(authentication.hasGrantedAuthority("ANY_PRIVILEGE"));
    }

    /**
     * Test hasGrantedAuthority with null authority name
     */
    @Test
    public void test_hasGrantedAuthority_null_authority_name()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(authority1).getAuthority();
            will(returnValue("READ_PRIVILEGE"));

            oneOf(authority2).getAuthority();
            will(returnValue("WRITE_PRIVILEGE"));
        }});

        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertFalse(authentication.hasGrantedAuthority(null));
        mockery.assertIsSatisfied();
    }

//    /**
//     * Test canAccessLinkedItem returns true when item is accessible
//     */
//    @Test
//    public void test_canAccessLinkedItem_accessible()
//    {
//        final String linkedItemType = "Module";
//        final Long linkedItemId = 123L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(linkedItemType));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(linkedItemId));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        assertTrue(authentication.canAccessLinkedItem(linkedItemType, linkedItemId));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test canAccessLinkedItem returns false when item is not accessible
//     */
//    @Test
//    public void test_canAccessLinkedItem_not_accessible()
//    {
//        final String linkedItemType = "Module";
//        final Long linkedItemId = 123L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue("Flow"));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        assertFalse(authentication.canAccessLinkedItem(linkedItemType, linkedItemId));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test canAccessLinkedItem with null policy link
//     */
//    @Test
//    public void test_canAccessLinkedItem_null_policy_link()
//    {
//        final String linkedItemType = "Module";
//        final Long linkedItemId = 123L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(null));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        assertFalse(authentication.canAccessLinkedItem(linkedItemType, linkedItemId));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedModuleIds returns module IDs
//     */
//    @Test
//    public void test_getLinkedModuleIds()
//    {
//        final Long moduleId1 = 100L;
//        final Long moduleId2 = 200L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//        policyAuthorities.add(policy2);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(IkasanAuthentication.MODULE));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(moduleId1));
//
//            oneOf(policy2).getPolicyLink();
//            will(returnValue(policyLink2));
//
//            oneOf(policyLink2).getPolicyLinkType();
//            will(returnValue(policyLinkType2));
//
//            oneOf(policyLinkType2).getName();
//            will(returnValue(IkasanAuthentication.MODULE));
//
//            oneOf(policyLink2).getTargetId();
//            will(returnValue(moduleId2));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> moduleIds = authentication.getLinkedModuleIds();
//
//        assertNotNull(moduleIds);
//        assertEquals(2, moduleIds.size());
//        assertTrue(moduleIds.contains(moduleId1));
//        assertTrue(moduleIds.contains(moduleId2));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedFlowIds returns flow IDs
//     */
//    @Test
//    public void test_getLinkedFlowIds()
//    {
//        final Long flowId1 = 300L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(IkasanAuthentication.FLOW));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(flowId1));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> flowIds = authentication.getLinkedFlowIds();
//
//        assertNotNull(flowIds);
//        assertEquals(1, flowIds.size());
//        assertTrue(flowIds.contains(flowId1));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedBusinessStreamIds returns business stream IDs
//     */
//    @Test
//    public void test_getLinkedBusinessStreamIds()
//    {
//        final Long businessStreamId1 = 400L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(IkasanAuthentication.BUSINESS_STREAM));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(businessStreamId1));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> businessStreamIds = authentication.getLinkedBusinessStreamIds();
//
//        assertNotNull(businessStreamIds);
//        assertEquals(1, businessStreamIds.size());
//        assertTrue(businessStreamIds.contains(businessStreamId1));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedMappingConfigurationIds returns mapping configuration IDs
//     */
//    @Test
//    public void test_getLinkedMappingConfigurationIds()
//    {
//        final Long mappingConfigId1 = 500L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(IkasanAuthentication.MAPPING_CONFIGURATION));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(mappingConfigId1));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> mappingConfigIds = authentication.getLinkedMappingConfigurationIds();
//
//        assertNotNull(mappingConfigIds);
//        assertEquals(1, mappingConfigIds.size());
//        assertTrue(mappingConfigIds.contains(mappingConfigId1));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedModuleIds with mixed policy link types
//     */
//    @Test
//    public void test_getLinkedModuleIds_mixed_types()
//    {
//        final Long moduleId1 = 100L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//        policyAuthorities.add(policy2);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(IkasanAuthentication.MODULE));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(moduleId1));
//
//            oneOf(policy2).getPolicyLink();
//            will(returnValue(policyLink2));
//
//            oneOf(policyLink2).getPolicyLinkType();
//            will(returnValue(policyLinkType2));
//
//            oneOf(policyLinkType2).getName();
//            will(returnValue(IkasanAuthentication.FLOW));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> moduleIds = authentication.getLinkedModuleIds();
//
//        assertNotNull(moduleIds);
//        assertEquals(1, moduleIds.size());
//        assertTrue(moduleIds.contains(moduleId1));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test getLinkedModuleIds with null policy links
//     */
//    @Test
//    public void test_getLinkedModuleIds_null_policy_links()
//    {
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//        policyAuthorities.add(policy2);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(null));
//
//            oneOf(policy2).getPolicyLink();
//            will(returnValue(null));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> moduleIds = authentication.getLinkedModuleIds();
//
//        assertNotNull(moduleIds);
//        assertEquals(0, moduleIds.size());
//        mockery.assertIsSatisfied();
//    }

    /**
     * Test with null credentials
     */
    @Test
    public void test_null_credentials()
    {
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, null, previousLoginTimestamp);
        assertNull(authentication.getCredentials());
    }

    /**
     * Test with empty credentials
     */
    @Test
    public void test_empty_credentials()
    {
        String emptyCredentials = "";
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, emptyCredentials, previousLoginTimestamp);
        assertEquals(emptyCredentials, authentication.getCredentials());
    }

    /**
     * Test with zero previous login timestamp
     */
    @Test
    public void test_zero_previous_login_timestamp()
    {
        long zeroPreviousLoginTimestamp = 0L;
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, zeroPreviousLoginTimestamp);
        assertEquals(zeroPreviousLoginTimestamp, authentication.getPreviousLoginTimestamp());
    }

    /**
     * Test with negative previous login timestamp
     */
    @Test
    public void test_negative_previous_login_timestamp()
    {
        long negativePreviousLoginTimestamp = -1L;
        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, negativePreviousLoginTimestamp);
        assertEquals(negativePreviousLoginTimestamp, authentication.getPreviousLoginTimestamp());
    }

    /**
     * Test hasGrantedAuthority with case sensitivity
     */
    @Test
    public void test_hasGrantedAuthority_case_sensitive()
    {
        final String authorityName = "READ_PRIVILEGE";

        mockery.checking(new Expectations()
        {{
            oneOf(authority1).getAuthority();
            will(returnValue(authorityName));

            oneOf(authority2).getAuthority();
            will(returnValue("WRITE_PRIVILEGE"));
        }});

        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, authorities, credentials, previousLoginTimestamp);
        assertFalse(authentication.hasGrantedAuthority("read_privilege")); // Different case
        mockery.assertIsSatisfied();
    }

//    /**
//     * Test getLinkedIds with empty authorities
//     */
//    @Test
//    public void test_getLinkedModuleIds_empty_authorities()
//    {
//        List<GrantedAuthority> emptyAuthorities = new ArrayList<>();
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, emptyAuthorities, credentials, previousLoginTimestamp);
//        List<Long> moduleIds = authentication.getLinkedModuleIds();
//
//        assertNotNull(moduleIds);
//        assertEquals(0, moduleIds.size());
//    }
//
//    /**
//     * Test canAccessLinkedItem with matching type but different ID
//     */
//    @Test
//    public void test_canAccessLinkedItem_matching_type_different_id()
//    {
//        final String linkedItemType = "Module";
//        final Long linkedItemId = 123L;
//        final Long differentId = 456L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue(linkedItemType));
//
//            oneOf(policyLink1).getTargetId();
//            will(returnValue(differentId));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        assertFalse(authentication.canAccessLinkedItem(linkedItemType, linkedItemId));
//        mockery.assertIsSatisfied();
//    }
//
//    /**
//     * Test canAccessLinkedItem with different type but matching ID
//     */
//    @Test
//    public void test_canAccessLinkedItem_different_type_matching_id()
//    {
//        final String linkedItemType = "Module";
//        final Long linkedItemId = 123L;
//
//        List<GrantedAuthority> policyAuthorities = new ArrayList<>();
//        policyAuthorities.add(policy1);
//
//        mockery.checking(new Expectations()
//        {{
//            oneOf(policy1).getPolicyLink();
//            will(returnValue(policyLink1));
//
//            oneOf(policyLink1).getPolicyLinkType();
//            will(returnValue(policyLinkType1));
//
//            oneOf(policyLinkType1).getName();
//            will(returnValue("Flow"));
//        }});
//
//        IkasanAuthentication authentication = new IkasanAuthentication(true, principal, policyAuthorities, credentials, previousLoginTimestamp);
//        assertFalse(authentication.canAccessLinkedItem(linkedItemType, linkedItemId));
//        mockery.assertIsSatisfied();
//    }
}
