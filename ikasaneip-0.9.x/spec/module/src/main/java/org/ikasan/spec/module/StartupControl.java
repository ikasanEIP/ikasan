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
package org.ikasan.spec.module;

/**
 * JavaBean encapsulating startup control information for Flows
 * 
 * The following start types are defined:
 * 
 * AUTOMATIC: flow will be started by its container when the container is
 * initialised MANUAL: Flow will not be started by its container when
 * container is started/initialised, but will be manually startable at a later
 * stage DISABLED: Flow will not be started by its container when container
 * is started/initialised, and will not be manually startable at a later stage
 * 
 * @author The Ikasan Development Team
 * 
 */
public interface StartupControl
{
     /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName();
    /**
     * Accessor for flowName
     * 
     * @return flowName
     */
    public String getFlowName();
    /**
     * Accessor for startupType
     * 
     * @return startupType
     */
    public StartupType getStartupType();

    /**
     * Setter for startupType
     * 
     * @param action
     */
    public void setStartupType(StartupType startupType);

    public boolean isAutomatic();

    public boolean isManual();

    public boolean isDisabled();

    /**
     * Accessor for comment
     * 
     * @return comment
     */
    public String getComment();

    /**
     * Mutator for comment
     * 
     * @param comment
     */
    public void setComment(String comment);
}
