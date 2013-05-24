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
package org.ikasan.module.startup;

import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;

/**
 * JavaBean encapsulating startup control information for Module flows.
 * 
 * The following start types are defined:
 * 
 * AUTOMATIC: flow will be started by its container when the container is initialised. 
 * MANUAL: Flow will not be started by its container when
 * container is started/initialised, but will be manually startable at a later stage. 
 * DISABLED: Flow cannot be started either automatically or manually.
 * 
 * @author The Ikasan Development Team
 * 
 */
public class StartupControlImpl implements StartupControl
{
    /**
     * Name of the module with which the target Flow is associated
     */
    private String moduleName;

    /**
     * Name of the target Flow
     */
    private String flowName;

    /**
     * Startup type
     */
    private StartupType startupType;

    /**
     * Comment
     */
    private String comment;

    /**
     * Identity key
     */
    private Long id;

    /**
     * No args constructor, as required by ceratin ORM tools
     */
    @SuppressWarnings("unused")
    private StartupControlImpl()
    {
    }

    /**
     * Constructor
     * 
     * @param moduleName - Name of the module with which the target Flow is
     *            associated
     * @param flowName - Name of the target Flow
     */
    public StartupControlImpl(String moduleName, String flowName)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.startupType = StartupType.MANUAL;
    }

    /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Setter for moduleName
     * 
     * @param moduleName
     */
    @SuppressWarnings("unused")
    private void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Accessor for flowName
     * 
     * @return flowName
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * Setter for flowName
     * 
     * @param flowName
     */
    @SuppressWarnings("unused")
    private void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * Accessor for startupType
     * 
     * @return startupType
     */
    public StartupType getStartupType()
    {
        return startupType;
    }

    /**
     * Setter for startupType
     * 
     * @param action
     */
    public void setStartupType(StartupType startupType)
    {
        this.startupType = startupType;
    }

    @SuppressWarnings("unused")
    private String getStartupTypeString()
    {
        return startupType.toString();
    }

    @SuppressWarnings("unused")
    private void setStartupTypeString(String startupTypeString)
    {
        this.startupType = StartupType.valueOf(startupTypeString);
    }

    /**
     * Accessor for id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Setter for id
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    public boolean isAutomatic()
    {
        return startupType != null && startupType.equals(StartupType.AUTOMATIC);
    }

    public boolean isManual()
    {
        return startupType != null && startupType.equals(StartupType.MANUAL);
    }

    public boolean isDisabled()
    {
        return startupType != null && startupType.equals(StartupType.DISABLED);
    }

    /**
     * Accessor for comment
     * 
     * @return comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Mutator for comment
     * 
     * @param comment
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
