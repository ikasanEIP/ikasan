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
package org.ikasan.dashboard.ui.administration.window;

import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.administration.panel.PolicyPanel;
import org.ikasan.dashboard.ui.administration.panel.RolePanel;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.ikasan.systemevent.service.SystemEventService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PolicyWindow extends Window
{
	private Logger logger = LoggerFactory.getLogger(PolicyWindow.class);

	private static final long serialVersionUID = -3347325521531925322L;

	private UserService userService;
	private SecurityService securityService;
	private SystemEventService systemEventService;
	private Policy policy;


	public PolicyWindow(UserService userService, SecurityService securityService,
                        SystemEventService systemEventService, Policy policy)
	{
		super();
		this.userService = userService;
		if (this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"securityService cannot be null!");
		}
		this.systemEventService = systemEventService;
		if (this.systemEventService == null)
		{
			throw new IllegalArgumentException(
					"systemEventService cannot be null!");
		}
		this.policy = policy;
		if (this.policy == null)
		{
			throw new IllegalArgumentException(
					"role cannot be null!");
		}

		init();
	}


	public void init()
	{
		this.setModal(true);
		this.setResizable(false);

		this.setWidth("90%");
		this.setHeight("90%");

		PolicyPanel panel = new PolicyPanel(userService, securityService, systemEventService);
		panel.enter(policy);
		this.setContent(panel);
	}
}
