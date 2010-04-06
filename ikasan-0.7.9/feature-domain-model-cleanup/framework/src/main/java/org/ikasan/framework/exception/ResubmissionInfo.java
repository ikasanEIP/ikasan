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
package org.ikasan.framework.exception;

/**
 * This class represents a part (<code>ResubmissionInfo</code>) of the
 * default exception message structure. <p/>
 * 
 * @author Ikasan Development Team
 */
public class ResubmissionInfo
{

    /** channelName can be removed once BW CMI has been deprecated */
    private String channelName = null;

    /** modelName can be removed once BW CMI has been deprecated */
    private String modelName = null;

    /** essentially the interface name */
    private String componentGroupName = null;

    /** bean component in the interface */
    private String componentName = null;

    /**
     * Creates a new <code>ResubmissionInfo</code> instance.
     * 
     * @param channelName 
     * @param modelName 
     * @param componentGroupName 
     * @param componentName 
     */
    public ResubmissionInfo(final String channelName,
                            final String modelName,
                            final String componentGroupName,
                            String componentName)
    {
        this.channelName = channelName;
        this.modelName = modelName;
        this.componentGroupName = componentGroupName;
        this.componentName = componentName;
    }

    /**
     * Creates a new <code>ResubmissionInfo</code> instance.
     * 
     */
    public ResubmissionInfo()
    {
        // Do Nothing
    }

    /**
     * Sets the channel name.
     * 
     * @param channelName 
     */
    public void setChannelName(final String channelName)
    {
        this.channelName = channelName;
    }

    /**
     * Returns the channel name.
     * 
     * @return the channel name.
     */
    public String getChannelName()
    {
        return this.channelName;
    }

    /**
     * Sets the component model name.
     * 
     * @param modelName 
     */
    public void setModelName(final String modelName)
    {
        this.modelName = modelName;
    }

    /**
     * Returns the component model name.
     * 
     * @return the component model name.
     */
    public String getModelName()
    {
        return this.modelName;
    }

    /**
     * Sets the component group name.
     * 
     * @param componentGroupName 
     */
    public void setComponentGroupName(final String componentGroupName)
    {
        this.componentGroupName = componentGroupName;
    }

    /**
     * Returns the component group name.
     * 
     * @return the component group name.
     */
    public String getComponentGroupName()
    {
        return this.componentGroupName;
    }

    /**
     * Sets the component name.
     * 
     * @param componentName 
     */
    public void setComponentName(final String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * Returns the component name.
     * 
     * @return the component name.
     */
    public String getComponentName()
    {
        return this.componentName;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        return "channelName: [" + this.channelName + "], " + "modelName: ["
                + this.modelName + "]" + "componentGroupName: ["
                + this.componentGroupName + "]" + "componentName: ["
                + this.componentName + "]";
    }

}
