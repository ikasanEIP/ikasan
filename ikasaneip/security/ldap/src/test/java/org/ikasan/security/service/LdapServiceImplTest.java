package org.ikasan.security.service;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LdapServiceImplTest {


    @Mock
    private SecurityDao securityDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    public void testIsValidEncoding_invalidDepartment() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Special chars department ¡—";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);

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