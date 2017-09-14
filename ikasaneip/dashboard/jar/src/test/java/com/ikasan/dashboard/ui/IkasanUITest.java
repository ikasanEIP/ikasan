/*
 * $Id$  
 * $URL$
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
package com.ikasan.dashboard.ui;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Ignore
public class IkasanUITest extends TestBenchTestCase 
{
	   @Before
	   public void setUp() throws Exception {
		   
		   System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\chromedriver.exe");
		   
		   setDriver(new ChromeDriver());
	   }

	   @After
	   public void tearDown() throws Exception {
	       getDriver().quit();
	   }

	   @Test
	   public void testLoginFail() 
	   {
		   getDriver().get("http://svc-Ikasan Development Team:8080/ikasan-dashboard/?debug");
		   
		   ButtonElement loginLinkButton = $(ButtonElement.class).caption("Login").first();
		   loginLinkButton.click();
		   
		   Assert.assertNotNull(loginLinkButton);
		   
		   TextFieldElement usernameTextField = $(TextFieldElement.class).caption("Username").first();
		   Assert.assertNotNull(usernameTextField);
		   
		   PasswordFieldElement passwordPasswordField = $(PasswordFieldElement.class).caption("Password").first();
		   Assert.assertNotNull(passwordPasswordField);
		   
		   usernameTextField.setValue("Ikasan Development Team");
		   passwordPasswordField.setValue("StJean2015");
		   
		   ButtonElement loginButton = $$(WindowElement.class).caption("Login")
				   .$(ButtonElement.class).caption("Login").first();
		   Assert.assertNotNull(loginButton);
		   
		   loginButton.click();
		   
		   MenuBarElement menuBar2 = $(MenuBarElement.class).get(1);
		   
		   Assert.assertNotNull(menuBar2);
	   }
}
