package org.ikasan.security.service;

import com.unboundid.ldap.listener.Base64PasswordEncoderOutputFormatter;
import com.unboundid.ldap.listener.ClearInMemoryPasswordEncoder;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;
import org.ikasan.security.LdapSecurityTestAutoConfiguration;
import org.ikasan.security.SecurityAutoConfiguration;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import javax.naming.NamingException;

import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.ldap.CommunicationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityAutoConfiguration.class, LdapSecurityTestAutoConfiguration.class})
public class LdapServiceImplTest {

    private InMemoryDirectoryServer inMemoryDirectoryServer;

    @Autowired
    InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig;

    @Autowired
    private String ldapServerUrl;

    @Mock
    private SecurityDao securityDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    public LdapServiceImpl ldapService;

    @Before
    public void setup() throws LDAPException, IOException {
        MockitoAnnotations.initMocks(this);
        this.setupLdapServer();
    }

    public void setupLdapServer() throws LDAPException, IOException
    {
        this.inMemoryDirectoryServerConfig.setPasswordEncoders(new ClearInMemoryPasswordEncoder("{CLEAR}", null),
            new ClearInMemoryPasswordEncoder("{BASE64}", Base64PasswordEncoderOutputFormatter.getInstance()));
        this.inMemoryDirectoryServer = new InMemoryDirectoryServer(this.inMemoryDirectoryServerConfig);
        inMemoryDirectoryServer.importFromLDIF(
            true,
            new LDIFReader(new File(new File(".").getCanonicalPath() + "/src/test/resources/data.ldif")));

        inMemoryDirectoryServer.startListening();

        ldapService = new LdapServiceImpl(this.securityDao, this.userDao, this.passwordEncoder
            , 1000, 1000);
    }

    @After
    public void teardownLdapServer()
    {
        // Disconnect from the server and cause the server to shut down.

        inMemoryDirectoryServer.clear();
        inMemoryDirectoryServer.shutDown(true);
    }

    @Test(expected = CommunicationException.class)
    public void test_connect_timeout_exception() throws LdapServiceException {
        AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setLdapServerUrl("ldap://server.ldap.com:389");
        authMethod.setOrder(1L);
        authMethod.setEnabled(true);
        authMethod.setLdapBindUserDn("cn=Directory Manager");
        authMethod.setLdapBindUserPassword("password");
        authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
        authMethod.setLdapUserSearchFilter("(uid={0})");
        ldapService = new LdapServiceImpl(this.securityDao, this.userDao, this.passwordEncoder
            , 1000, 1000);

        try {
            ldapService.synchronize(authMethod);
        }
        catch (Exception exception) {
            Optional<Throwable> rootCause = Stream.iterate(exception, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();
            Assert.assertEquals(SocketTimeoutException.class, rootCause.get().getClass());
            Assert.assertEquals("Connect timed out", rootCause.get().getMessage());
            throw exception;
        }
    }

    @Test(expected = UncategorizedLdapException.class)
    public void test_read_timeout_exception() throws LdapServiceException {
        AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setLdapServerUrl(this.ldapServerUrl);
        authMethod.setOrder(1L);
        authMethod.setEnabled(true);
        authMethod.setLdapBindUserDn("cn=Directory Manager");
        authMethod.setLdapBindUserPassword("password");
        authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
        authMethod.setApplicationSecurityBaseDn("ou=application,ou=IL-Sunset,dc=slidev,dc=org");
        authMethod.setLdapUserSearchFilter("(uid={0})");
        authMethod.setGroupSynchronisationFilter("(uid={0})");
        ldapService = new LdapServiceImpl(this.securityDao, this.userDao, this.passwordEncoder
            , 1, 1000);

        try {
            ldapService.synchronize(authMethod);
        }
        catch (Exception exception) {
            Optional<Throwable> rootCause = Stream.iterate(exception, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();
            Assert.assertEquals(NamingException.class, rootCause.get().getClass());
            Assert.assertEquals("LDAP response read timed out, timeout used: 1 ms.", rootCause.get().getMessage());
            throw exception;
        }
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
    public void testIsValidEncoding_happyPath_with_kanji_characters() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "username 例外クラス";
        ldapUser.department = "Sample department 例外クラス";
        ldapUser.firstName = "Sample firstname 例外クラス";
        ldapUser.surname = "Sample surname 例外クラス";
        ldapUser.description = "Sample description 例外クラス";
        ldapUser.email = "test@there.com 例外クラス";
        ldapUser.memberOf = new String[]{"group 1 例外クラス ", "group 2 例外クラス"};

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
        ldapUser.department = "Special chars department ¡" + '\uD835';

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
        ldapUser.firstName = "Special chars firstname ¡—" + '\uD835';

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
        ldapUser.surname = "Special chars surname ¡—" + '\uD835';

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
        ldapUser.email = "Special chars email ¡—" + '\uD835';

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
        ldapUser.description = "Special chars description ¡—" + '\uD835';

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
        ldapUser.accountName = "invalidName-with-EmDash—" + '\uD835';
        ldapUser.department = "Some department";

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);
    }

    @Test
    public void testIsValidEncoding_invalidGroup() {

        LdapServiceImpl.LdapUser ldapUser = ldapService.new LdapUser();
        ldapUser.accountName = "userName";
        ldapUser.department = "Some department";
        ldapUser.memberOf = new String[]{"Special chars group ¡—" + '\uD835', "another group"};

        boolean validEncoding = ldapService.isValidEncoding(ldapUser);

        Assert.assertTrue(!validEncoding);
    }

}