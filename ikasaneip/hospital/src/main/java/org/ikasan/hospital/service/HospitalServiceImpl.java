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
package org.ikasan.hospital.service;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.ikasan.hospital.dao.HospitalDao;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.Serialiser;


/**
 * User and Authority service interface
 * 
 * @author Ikasan Development Team
 * 
 */
public class HospitalServiceImpl implements HospitalService<byte[]>
{
	/** running state string constant */
    private static String RUNNING = "running";
    
    /** stopped state string constant */
    private static String STOPPED = "stopped";
    
    /** recovering state string constant */
    private static String RECOVERING = "recovering";
    
    /** stoppedInError state string constant */
    private static String STOPPED_IN_ERROR = "stoppedInError";
    
    /** paused state string constant */
    private static String PAUSED = "paused";
    
	private static Logger logger = Logger.getLogger(HospitalServiceImpl.class);
	
	private ModuleContainer moduleContainer;
	private HospitalDao hospitalDao;
	private ExclusionManagementService exclusionManagementService;
	
	/**
	 * Constructor
	 * 
	 * @param moduleContainer
	 */
	public HospitalServiceImpl(ModuleContainer moduleContainer, HospitalDao hospitalDao,
			ExclusionManagementService exclusionManagementService)
	{
		super();
		this.moduleContainer = moduleContainer;
		if(this.moduleContainer == null)
		{
			throw new IllegalArgumentException("moduleContainer cannot be null!");
		}
		this.hospitalDao = hospitalDao;
		if(this.hospitalDao == null)
		{
			throw new IllegalArgumentException("hospitalDao cannot be null!");
		}
		this.exclusionManagementService = exclusionManagementService;
		if(this.exclusionManagementService == null)
		{
			throw new IllegalArgumentException("exclusionManagementService cannot be null!");
		}
	}


	/* (non-Javadoc)
	 * @see org.ikasan.hospital.service.HospitalService#resubmit(java.lang.String, java.lang.String, java.lang.Object, java.security.Principal)
	 */
	@Override
	public void resubmit(String moduleName, String flowName, String errorUri, byte[] event,
			Principal principal)
	{
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		Flow flow = module.getFlow(flowName);
		
		if(flow.getState().equals(STOPPED) || flow.getState().equals(STOPPED_IN_ERROR))
		{
			throw new RuntimeException("Events cannot be resubmitted when the flow that is being resubmitted to is in a " +
					flow.getState() + " state.  Module[" + moduleName +"] Flow[" + flowName + "]");
		}
		
		FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
		
		ResubmissionService resubmissionService = flowConfiguration.getResubmissionService();
		
		if(resubmissionService == null)
		{
			throw new RuntimeException("The resubmission service on the flow you are resubmitting to is null. This is most liekly due to " +
					"the resubmission service not being set on the flow factory for the flow you are resubmitting to.");
		}
		
		Serialiser serialiser = flow.getSerialiserFactory().getDefaultSerialiser();
			
		Object deserialisedEvent = serialiser.deserialise(event);
		
		logger.debug("deserialisedEvent " + deserialisedEvent);
		
		resubmissionService.submit(deserialisedEvent);
		
		ExclusionEventAction action = new ExclusionEventAction(errorUri, principal.getName(),
				ExclusionEventAction.RESUBMIT, event, moduleName, flowName);
		
		exclusionManagementService.delete(errorUri);
		
		this.hospitalDao.saveOrUpdate(action);
	}


	/* (non-Javadoc)
	 * @see org.ikasan.hospital.service.HospitalService#ignore(java.lang.String, java.security.Principal)
	 */
	@Override
	public void ignore(String moduleName, String flowName, String errorUri, byte[] event, Principal principal)
	{
		ExclusionEventAction action = new ExclusionEventAction(errorUri, principal.getName(),
				ExclusionEventAction.IGNORED, event, moduleName, flowName);
		
		exclusionManagementService.delete(errorUri);

		this.hospitalDao.saveOrUpdate(action);
	}
}
