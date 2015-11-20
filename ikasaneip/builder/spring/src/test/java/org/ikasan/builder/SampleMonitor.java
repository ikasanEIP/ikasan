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
package org.ikasan.builder;

import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;

import java.util.List;

/**
 * Sample monitor implementation for testing.
 * @author Ikasan Development Team
 * 
 */
public class SampleMonitor implements Monitor, ManagedService
{

    /* (non-Javadoc)
     * @see org.ikasan.spec.monitor.Monitor#notifyMonitor(java.lang.Object)
     */
    public void invoke(Object arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setEnvironment(String environmentName) 
    {

    }

    @Override
    public String getEnvironment() 
    {
        return null;
    }

    @Override
    public List<Notifier> getNotifiers() 
    {
        return null;
    }

    @Override
    public void setNotifiers(List list) 
    {

    }

    @Override
    public void destroy()
    {
    }

	/* (non-Javadoc)
	 * @see org.ikasan.spec.monitor.Monitor#setModuleName(java.lang.String)
	 */
	@Override
	public void setModuleName(String moduleName)
	{
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.ikasan.spec.monitor.Monitor#getModuleName()
	 */
	@Override
	public String getModuleName()
	{
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.ikasan.spec.monitor.Monitor#setFlowName(java.lang.String)
	 */
	@Override
	public void setFlowName(String flowName)
	{
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.ikasan.spec.monitor.Monitor#getFlowName()
	 */
	@Override
	public String getFlowName()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
