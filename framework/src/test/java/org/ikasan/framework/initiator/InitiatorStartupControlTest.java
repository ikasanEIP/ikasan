package org.ikasan.framework.initiator;

import junit.framework.Assert;

import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.junit.Test;

public class InitiatorStartupControlTest {

	@Test
	public void testIsAutomatic() {
		InitiatorStartupControl initiatorStartupControl = new InitiatorStartupControl("moduleName","initiatorName");
		Assert.assertFalse(initiatorStartupControl.isAutomatic());
		initiatorStartupControl.setStartupType(StartupType.AUTOMATIC);
		Assert.assertTrue(initiatorStartupControl.isAutomatic());
	}

	@Test
	public void testIsManual() {
		InitiatorStartupControl initiatorStartupControl = new InitiatorStartupControl("moduleName","initiatorName");
		Assert.assertTrue(initiatorStartupControl.isManual());
		initiatorStartupControl.setStartupType(StartupType.MANUAL);
		Assert.assertTrue(initiatorStartupControl.isManual());
		initiatorStartupControl.setStartupType(StartupType.AUTOMATIC);
		Assert.assertFalse(initiatorStartupControl.isManual());
	}

	@Test
	public void testIsDisabled() {
		InitiatorStartupControl initiatorStartupControl = new InitiatorStartupControl("moduleName","initiatorName");
		Assert.assertFalse(initiatorStartupControl.isDisabled());
		initiatorStartupControl.setStartupType(StartupType.DISABLED);
		Assert.assertTrue(initiatorStartupControl.isDisabled());
	}

}
