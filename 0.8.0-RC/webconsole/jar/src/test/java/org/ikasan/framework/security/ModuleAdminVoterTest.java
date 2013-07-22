/*
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */

package org.ikasan.framework.security;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.vote.AccessDecisionVoter;

/**
 * @author The Ikasan Development Team
 * 
 */
public class ModuleAdminVoterTest {

	/**
	 * Mockery for mocking concrete classes
	 */
	private Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/**
	 * Class under test
	 */
	private ModuleAdminVoter moduleAdminVoter = new ModuleAdminVoter();

	/**
	 * Test method for
	 * {@link org.ikasan.framework.security.ModuleAdminVoter#supports(ConfigAttribute)}
	 * .
	 */
	@Test
	public void testSupportsConfigAttribute() {
		Assert
				.assertTrue(
						"should return true for MODULE_ADMIN",
						moduleAdminVoter
								.supports(mockConfigAttribute(ModuleAdminVoter.MODULE_ADMIN_ATTRIBUTE)));

		mockery.assertIsSatisfied();

		Assert.assertFalse(
				"should return false for anything other than MODULE_ADMIN",
				moduleAdminVoter
						.supports(mockConfigAttribute("someOtherValue")));

		mockery.assertIsSatisfied();
	}

	/**
	 * @param configAttribute
	 * @param configAttributeValue
	 */
	private ConfigAttribute mockConfigAttribute(
			final String configAttributeValue) {
		final ConfigAttribute configAttribute = mockery
				.mock(ConfigAttribute.class);
		mockery.checking(new Expectations() {
			{
				one(configAttribute).getAttribute();
				will(returnValue(configAttributeValue));
			}
		});
		return configAttribute;
	}

	private GrantedAuthority mockModuleAdminRole(final String moduleName) {
		final GrantedAuthority moduleAdminRole = mockery
				.mock(GrantedAuthority.class);
		mockery.checking(new Expectations() {
			{
				allowing(moduleAdminRole).getAuthority();
				will(returnValue("ADMIN_" + moduleName));
			}
		});
		return moduleAdminRole;
	}

	/**
	 * Test that the voter will abstain if the configDefinitionAttribute does
	 * not contain the MODULE_ADMIN attribute
	 * 
	 * Test method for
	 * {@link org.ikasan.framework.security.ModuleAdminVoter#vote(Authentication, Object, ConfigAttributeDefinition)}
	 * .
	 */
	@Test
	public void testVote_withUnsupportedConfigAttributeWillVoteABSTAIN() {
		// mock a configAttributeDefinition that contains some attribute other
		// than MODULE_ADMIN, which is the attribute ModuleAdminVoter supports
		ConfigAttributeDefinition configAttributeDefinition = new ConfigAttributeDefinition(
				mockConfigAttribute("someOtherAttribute"));

		final String thisModuleName = "moduleName";

		// mock an authentication that contains a GrantedAuthority representing
		// the ADMIN role for this module
		final Authentication authentication = mockery
				.mock(Authentication.class);
		mockery.checking(new Expectations() {
			{
				one(authentication).getAuthorities();
				will(returnValue(new GrantedAuthority[] { mockModuleAdminRole(thisModuleName) }));
			}
		});

		// mock the method call that is being secured - actually all we care
		// about here is the first method argument
		final ReflectiveMethodInvocation reflectiveMethodInvocation = new MockReflectiveMethodInvocation(
				null, null, null, new Object[] { thisModuleName }, null, null);

		int vote = moduleAdminVoter.vote(authentication,
				reflectiveMethodInvocation, configAttributeDefinition);

		Assert
				.assertEquals(
						"should vote ACCESS_ABSTAIN when MODULE_ADMIN config attribute does not exist",
						AccessDecisionVoter.ACCESS_ABSTAIN, vote);
		mockery.assertIsSatisfied();
	}

	/**
	 * Test that the voter will vote in the affirmative when the module admin
	 * role is held within the authentication AND the MODULE_ADMIN config
	 * attribute is present
	 * 
	 * Test method for
	 * {@link org.ikasan.framework.security.ModuleAdminVoter#vote(Authentication, Object, ConfigAttributeDefinition)}
	 * .
	 */
	@Test
	public void testVote_withSupportedConfigAttributeWillVoteACCESS_GRANTED_whenAuthenticationIncludesModuleAdminRoleForThisModule() {
		// mock a configAttributeDefinition that contains attribute
		// MODULE_ADMIN, which the ModuleAdminVoter supports
		ConfigAttributeDefinition configAttributeDefinition = new ConfigAttributeDefinition(
				mockConfigAttribute(ModuleAdminVoter.MODULE_ADMIN_ATTRIBUTE));

		final String thisModuleName = "moduleName";

		// mock an authentication that contains a GrantedAuthority representing
		// the ADMIN role for this module
		final Authentication authentication = mockery
				.mock(Authentication.class);
		mockery.checking(new Expectations() {
			{
				one(authentication).getAuthorities();
				will(returnValue(new GrantedAuthority[] { mockModuleAdminRole(thisModuleName) }));
			}
		});

		// mock the method call that is being secured - actually all we care
		// about here is the first method argument
		final ReflectiveMethodInvocation reflectiveMethodInvocation = new MockReflectiveMethodInvocation(
				null, null, null, new Object[] { thisModuleName }, null, null);

		int vote = moduleAdminVoter.vote(authentication,
				reflectiveMethodInvocation, configAttributeDefinition);

		Assert
				.assertEquals(
						"should vote ACCESS_GRANTED when a MODULE_ADMIN config attribute exists, AND the user holds the ADMIN_{moduleName} role",
						AccessDecisionVoter.ACCESS_GRANTED, vote);
		mockery.assertIsSatisfied();
	}

	/**
	 * Test that the voter will vote in the negative when the module admin role
	 * is not held within the authentication AND the MODULE_ADMIN config
	 * attribute is present
	 * 
	 * Test method for
	 * {@link org.ikasan.framework.security.ModuleAdminVoter#vote(Authentication, Object, ConfigAttributeDefinition)}
	 * .
	 */
	@Test
	public void testVote_withSupportedConfigAttributeWillVoteACCESS_DENIED_whenAuthenticationDoesNotIncludeModuleAdminRoleForThisModule() {
		// mock a configAttributeDefinition that contains attribute
		// MODULE_ADMIN, which the ModuleAdminVoter supports
		ConfigAttributeDefinition configAttributeDefinition = new ConfigAttributeDefinition(
				mockConfigAttribute(ModuleAdminVoter.MODULE_ADMIN_ATTRIBUTE));

		final String thisModuleName = "moduleName";

		// mock an authentication that does not contain a GrantedAuthority
		// representing the ADMIN role for this module
		final Authentication authentication = mockery
				.mock(Authentication.class);
		mockery.checking(new Expectations() {
			{
				one(authentication).getAuthorities();
				will(returnValue(new GrantedAuthority[] {}));
			}
		});

		// mock the method call that is being secured - actually all we care
		// about here is the first method argument
		final ReflectiveMethodInvocation reflectiveMethodInvocation = new MockReflectiveMethodInvocation(
				null, null, null, new Object[] { thisModuleName }, null, null);

		int vote = moduleAdminVoter.vote(authentication,
				reflectiveMethodInvocation, configAttributeDefinition);

		Assert
				.assertEquals(
						"should vote ACCESS_DENIED when a MODULE_ADMIN config attribute exists, BUT the user doen not hold the ADMIN_{moduleName} role",
						AccessDecisionVoter.ACCESS_DENIED, vote);
		mockery.assertIsSatisfied();
	}

	class MockReflectiveMethodInvocation extends ReflectiveMethodInvocation {
		/**
		 * Extended, just to expose the protected constructor of ReflectiveMethodInvocation
		 * 
		 * @param proxy
		 * @param target
		 * @param method
		 * @param arguments
		 * @param targetClass
		 * @param interceptorsAndDynamicMethodMatchers
		 */
		protected MockReflectiveMethodInvocation(Object proxy, Object target,
				Method method, Object[] arguments, Class targetClass,
				List interceptorsAndDynamicMethodMatchers) {
			super(proxy, target, method, arguments, targetClass,
					interceptorsAndDynamicMethodMatchers);
			// TODO Auto-generated constructor stub
		}
	}

}
