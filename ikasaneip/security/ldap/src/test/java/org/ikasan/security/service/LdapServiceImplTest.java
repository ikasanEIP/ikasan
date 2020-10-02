package org.ikasan.security.service;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.AuthenticationMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.when;

public class LdapServiceImplTest {

    @Mock
    private SecurityDao securityDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationMethod authenticationMethod;

    @InjectMocks
    public LdapServiceImpl ldapService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValidEncoding_happyPath() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Sample department";
        ldapUser.firstName = "Sample firstname";
        ldapUser.surname = "Sample surname";
        ldapUser.description = "Sample description";
        ldapUser.email = "test@there.com";
        ldapUser.memberOf = new String[]{"group 1", "group 2"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_withoutMemberGroups() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Sample department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_nullDepartment() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidDepartment() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Special chars department ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

    @Test
    public void testIsValidEncoding_nullFirstName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.firstName = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidFirstName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.firstName = "Special chars firstname ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

    @Test
    public void testIsValidEncoding_nullSurname() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.surname = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidSurname() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.surname = "Special chars surname ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

    @Test
    public void testIsValidEncoding_nullEmail() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.email = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidEmail() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.email = "Special chars email ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

    @Test
    public void testIsValidEncoding_nullDescription() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.description = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidDescription() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.description = "Special chars description ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }


    @Test
    public void testIsValidEncoding_nullAccountName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = null;
        ldapUser.department = "Some department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(validEncoding);
    }

    @Test
    public void testIsValidEncoding_invalidAccountName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "invalidName-with-EmDash—";
        ldapUser.department = "Some department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

    @Test
    public void testIsValidEncoding_invalidGroup() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "userName";
        ldapUser.department = "Some department";
        ldapUser.memberOf = new String[]{"Special chars group ¡—", "another group"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

    }

}