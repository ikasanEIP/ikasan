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
package org.ikasan.framework.module.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.flow.initiator.dao.InitiatorCommandDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorCommand;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;

/**
 * Default implementation of <code>ModuleService</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleServiceImpl implements ModuleService
{
    private InitiatorCommandDao initiatorCommandDao;
    
    /**
     * Logger instance
     */
    private Logger logger = Logger.getLogger(ModuleServiceImpl.class);
  
    
    /**
     * runtime conatiner for holding modules
     */
    private ModuleContainer moduleContainer; 
    
    /**
     * Flag determining whether or not we should keep historic InitiatorCommands
     */
    private boolean keepHistoricInitiatorCommands = false;


    /**
     * Constructor
     * 
     * @param moduleContainer
     */
    public ModuleServiceImpl(ModuleContainer moduleContainer, InitiatorCommandDao initiatorCommandDao)
    {
        super();
        this.moduleContainer=moduleContainer;
        this.initiatorCommandDao = initiatorCommandDao;

    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModules()
     */
    public List<Module> getModules()
    {
        return moduleContainer.getModules();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModule(java.lang.String)
     */
    public Module getModule(String moduleName)
    {
        //TODO throw exception if moduleName is erroneous
        return moduleContainer.getModule(moduleName);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void stopInitiator(String moduleName, String initiatorName, String actor){
    	logger.info("stopInitiator : "+moduleName+"."+initiatorName+" requested by ["+actor+"]");
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        
        //create and persist an instance of InitiatorCommand
        InitiatorCommand initiatorCommand = new InitiatorCommand(moduleName, initiatorName, "stop",actor);
        initiatorCommandDao.save(initiatorCommand,keepHistoricInitiatorCommands);
        
        //now stop the Initiator
        initiator.stop();
    }

    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startInitiator(String moduleName, String initiatorName, String actor){
    	logger.info("startInitiator : "+moduleName+"."+initiatorName+" requested by ["+actor+"]");
    	Initiator initiator = resolveInitiator(moduleName, initiatorName);
        
        //create and persist an instance of InitiatorCommand
        InitiatorCommand initiatorCommand = new InitiatorCommand(moduleName, initiatorName, "start",actor);
        initiatorCommandDao.save(initiatorCommand, keepHistoricInitiatorCommands);
        
        //now start the Initiator
        initiator.start();
    }    
    
    private Initiator resolveInitiator(String moduleName, String initiatorName)
    {
        Module module = getModule(moduleName);
        if (module==null){
            throw new IllegalArgumentException("no such Module ["+moduleName+"]");
        }
        
        Initiator initiator = module.getInitiator(initiatorName);
        if (initiator==null){
            throw new IllegalArgumentException("no such Initiator ["+initiatorName+"] for Module ["+moduleName+"]");
        }
        return initiator;
    }
    
    
    
    


    



}
