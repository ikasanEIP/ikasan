/*
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
