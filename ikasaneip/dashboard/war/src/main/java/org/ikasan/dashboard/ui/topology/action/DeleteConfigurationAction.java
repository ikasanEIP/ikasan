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
package org.ikasan.dashboard.ui.topology.action;

import org.ikasan.dashboard.ui.framework.action.Action;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;

import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class DeleteConfigurationAction implements Action
{
	private Configuration configuration;
	private ConfigurationManagement configurationManagement;
	private Window window;

	/**
	 * @param configuration
	 * @param configurationManagement
	 */
	public DeleteConfigurationAction(Configuration configuration,
			ConfigurationManagement configurationManagement,
			Window window)
	{
		super();
		this.configuration = configuration;
		this.configurationManagement = configurationManagement;
		this.window = window;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.ui.framework.action.Action#exectuteAction()
	 */
	@Override
	public void exectuteAction()
	{
		this.configurationManagement.deleteConfiguration(this.configuration);
		window.close();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.ui.framework.action.Action#ignoreAction()
	 */
	@Override
	public void ignoreAction()
	{
		// do nothing
	}

}
