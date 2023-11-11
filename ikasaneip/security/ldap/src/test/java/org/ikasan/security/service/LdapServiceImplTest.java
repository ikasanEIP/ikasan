package org.ikasan.security.service;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.AuthenticationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIsValidEncoding_happyPath() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Sample department";
        ldapUser.firstName = "Sample firstname";
        ldapUser.surname = "Sample surname";
        ldapUser.description = "Sample description";
        ldapUser.email = "test@there.com";
        ldapUser.memberOf = new String[]{"group 1", "group 2"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_happyPath_with_kanji_characters() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username 例外クラス";
        ldapUser.department = "Sample department 例外クラス";
        ldapUser.firstName = "Sample firstname 例外クラス";
        ldapUser.surname = "Sample surname 例外クラス";
        ldapUser.description = "Sample description 例外クラス";
        ldapUser.email = "test@there.com 例外クラス";
        ldapUser.memberOf = new String[]{"group 1 例外クラス ", "group 2 例外クラス"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_withoutMemberGroups() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Sample department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_nullDepartment() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidDepartment() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.department = "Special chars department ¡" + '\uD835';

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

    @Test
    void testIsValidEncoding_nullFirstName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.firstName = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidFirstName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.firstName = "Special chars firstname ¡—" + '\uD835';

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

    @Test
    void testIsValidEncoding_nullSurname() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.surname = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidSurname() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.surname = "Special chars surname ¡—" + '\uD835';

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

    @Test
    void testIsValidEncoding_nullEmail() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.email = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidEmail() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.email = "Special chars email ¡—" + '\uD835';

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

    @Test
    void testIsValidEncoding_nullDescription() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.description = null;

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidDescription() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username";
        ldapUser.description = "Special chars description ¡—" + '\uD835';

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }


    @Test
    void testIsValidEncoding_nullAccountName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = null;
        ldapUser.department = "Some department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertTrue(validEncoding);
    }

    @Test
    void testIsValidEncoding_invalidAccountName() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "invalidName-with-EmDash—" + '\uD835';
        ldapUser.department = "Some department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

    @Test
    void testIsValidEncoding_invalidGroup() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "userName";
        ldapUser.department = "Some department";
        ldapUser.memberOf = new String[]{"Special chars group ¡—" + '\uD835', "another group"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        assertFalse(validEncoding);

    }

}