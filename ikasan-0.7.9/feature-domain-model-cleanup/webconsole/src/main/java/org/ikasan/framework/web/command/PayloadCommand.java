/*
 * $Id: 
 * $URL:
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
package org.ikasan.framework.web.command;

/**
 * A Wrapper class that can contain a payload along with the module name and initiator 
 * name to deliver that payload to.
 *  
 * @author Ikasan Development Team
 */
public class PayloadCommand
{
    /** The content of the payload */
    private String payloadContent;
    
    /** The name of the module */
    private String moduleName;
    
    /** The name of the initiator */
    private String initiatorName;

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     */
    public PayloadCommand(String moduleName, String initiatorName)
    {
        super();
        this.moduleName = moduleName;
        this.initiatorName = initiatorName;
    }

    /**
     * Get the initiator name
     * @return the initiator name
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }

    /**
     * Get the module name
     * @return the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Get the payload content
     * @return the payload content
     */
    public String getPayloadContent()
    {
        return payloadContent;
    }

    /**
     * Set the initiator name
     * @param initiatorName - the initiator name
     */
    public void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * Set the module name
     * @param moduleName - the module name
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Set the payload content name
     * @param payloadContent - the payload content
     */
    public void setPayloadContent(String payloadContent)
    {
        this.payloadContent = payloadContent;
    }
}
