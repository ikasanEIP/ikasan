package org.ikasan.dashboard.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.general.component.AboutIkasanDialog;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.Mockito.mock;

public class IkasanAppLayoutTest extends UITest {

    @Rule
    public TestName testName = new TestName();

    public void setup_expectations() {
        IkasanAuthentication mockIkasanAuthentication = mock(IkasanAuthentication.class);
        // Setup the mock authentication.
        SecurityContextHolder.getContext().setAuthentication(mockIkasanAuthentication);

        // Mock some of the requisite behaviour.
        Mockito.when(mockIkasanAuthentication.getName())
            .thenReturn("username");
        Mockito.when(this.userService.loadUserByUsername("username"))
            .thenReturn(user);
        Mockito.when(user.isRequiresPasswordChange())
            .thenReturn(false);

        if(testName.getMethodName().equals("test_admin_user_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_search_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_search_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_search_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SEARCH_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_wiretap_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_wiretap_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_wiretap_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_error_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_error_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_error_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_exclusion_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_exclusion_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_exclusion_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_replay_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_replay_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_replay_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_system_event_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SYSTEM_EVENT_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_system_event_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SYSTEM_EVENT_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_system_event_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.SYSTEM_EVENT_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_administration_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_administration_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_administration_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_group_administration_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_group_administration_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_group_administration_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_role_administration_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ROLE_ADMINISTRATION_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_role_administration_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ROLE_ADMINISTRATION_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_role_administration_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ROLE_ADMINISTRATION_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_policy_administration_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.POLICY_ADMINISTRATION_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_policy_administration_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.POLICY_ADMINISTRATION_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_policy_administration_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.POLICY_ADMINISTRATION_WRITE))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_directory_admin_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_ADMIN))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_directory_read_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_READ))
                .thenReturn(true);
        }
        else if(testName.getMethodName().equals("test_user_directory_write_security")) {
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
                .thenReturn(false);
            Mockito.when(mockIkasanAuthentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_WRITE))
                .thenReturn(true);
        }
    }

    @Test
    public void test_logout()
    {
       _click(_get(Button.class, spec -> spec.withId("logoutButton")));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void test_about_dialog()
    {
        _click(_get(Button.class, spec -> spec.withId("aboutButton")));

        Assertions.assertNotNull(_get(AboutIkasanDialog.class));
    }

    @Test
    public void test_admin_user_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_search_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_search_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_search_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_wiretap_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_wiretap_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_wiretap_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_error_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_error_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_error_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_exclusion_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_exclusion_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_exclusion_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_replay_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_replay_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_replay_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_system_event_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_system_event_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_system_event_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_administration_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_administration_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_administration_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_group_administration_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_group_administration_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_group_administration_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_role_administration_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_role_administration_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_role_administration_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_policy_administration_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_policy_administration_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_policy_administration_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_directory_read_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_directory_write_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }

    @Test
    public void test_user_directory_admin_security()
    {
        UI.getCurrent().navigate("");

        IkasanAppLayout ikasanAppLayout = _get(IkasanAppLayout.class);
        Assertions.assertNotNull(ikasanAppLayout);

        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "searchMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "visualisationMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "systemEventMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "groupManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "roleManagementMenuItem")).isVisible());
        Assertions.assertEquals(false, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "policyManagementMenuItem")).isVisible());
        Assertions.assertEquals(true, ((Component)ReflectionTestUtils.getField(ikasanAppLayout, "userDirectoryManagementMenuItem")).isVisible());
    }
}
