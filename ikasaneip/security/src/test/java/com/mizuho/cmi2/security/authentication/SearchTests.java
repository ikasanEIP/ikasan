package com.mizuho.cmi2.security.authentication;

import javax.annotation.Resource;

import org.ikasan.security.dao.HibernateSecurityDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * Unit test for {@link HibernateSecurityDao}
 * 
 * @author CMI2 Development Team
 * 
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"/hsqldb-config.xml", "/substitute-components.xml",
		"/mock-components.xml" })
public class SearchTests 
{
	@Resource
	InMemoryDirectoryServer inMemoryDirectoryServer;

	@Before
	public void setup() throws LDAPException
	{
		inMemoryDirectoryServer.importFromLDIF(
				true,
				"N:/sandbox/stewmi-sbox/workspace/ikasan/ikasaneip/security/src/test/resources/data.ldif");
		
		inMemoryDirectoryServer.startListening();
	}

	@Test
	public void test() throws LDAPException 
	{

		System.out.println("port=" + inMemoryDirectoryServer.getListenPort());
		System.out.println("address=" + inMemoryDirectoryServer.getListenAddress());

		// Get a client connection to the server and use it to perform various
		// operations.
		LDAPConnection conn = inMemoryDirectoryServer.getConnection();
		SearchResultEntry entry = conn
				.getEntry("CN=Donnell Alwyn,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");

		System.out.println(entry.getDN());
		System.out.println(entry.getAttribute("givenName").getValue());

		conn.close();
	}

	@After
	public void teardown()
	{
		// Disconnect from the server and cause the server to shut down.
		inMemoryDirectoryServer.shutDown(true);
	}
//	/**
//	 * @param args
//	 * @throws LDAPException
//	 * @throws UnknownHostException
//	 */
//	public static void main(String[] args) throws LDAPException,
//			UnknownHostException 
//	{
//		// Create the configuration to use for the server.
//		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(
//				"DC=uk,DC=mizuho-sc,DC=com");
//		java.net.InetAddress address = java.net.InetAddress.getLocalHost();
//		config.setListenerConfigs(new InMemoryListenerConfig(
//				"ldapServerListener", address, 33389, null, null, null));
//		config.addAdditionalBindCredentials("cn=Directory Manager", "password");
//		config.setSchema(null);
//
//		// Create the directory server instance, populate it with data from the
//		// "test-data.ldif" file, and start listening for client connections.
//		InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
//
//	}
}
