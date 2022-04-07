/* 
 * $Id: Module.java 3665 2011-04-27 09:09:41Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/spec/flow/src/main/java/org/ikasan/spec/module/Module.java $
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

import java.util.List;

/**
 * Ikasan module contracts.
 * 
 * @author Ikasan Development Team
 */
public interface Module<FLOW>
{
    /**
     * Set the module type on the metadata.
     *
     * @param moduleType
     */
    public void setType(ModuleType moduleType);

    /**
     * Get the module type.
     *
     * @return
     */
    public ModuleType getType();

    /**
     * Returns the url of the module.
     *
     * @return
     */
    public String getUrl();

    /**
     * Set the url of the module.
     *
     * @param url
     */
    public void setUrl(String url);

    /**
     * Returns the runtime version of the module
     *
     * @return version of the module
     */
    public String getVersion();

    /**
     * Returns the name of the module
     * 
     * @return name of the module
     */
    public String getName();

    /**
     * Returns a {@link List} of this module's <code>Flow</code>s
     * 
     * @return a {@link List} of <code>FLOW</code>.
     */
    public List<FLOW> getFlows();

    /**
     * Returns a <code>Flow</code> with this name.
     * If no flow of this name exists then it returns null.
     * 
     * @return a the <code>Flow</code> corresponding to the given name
     */
    public FLOW getFlow(String name);

    /**
     * Returns a human readable description of this module
     * 
     * @return String description
     */
    public String getDescription();

    /**
     * Sets a human readable description of this module
     * 
     * @param description
     */
    public void setDescription(String description);

    /**
     * Get the host that the module is running on
     *
     * @return
     */
    public String getHost();

    /**
     * Set the host that the module is running on.
     *
     * @param host
     */
    public void setHost(String host);

    /**
     * Get the port that the module is bound to.
     *
     * @return
     */
    public Integer getPort();

    /**
     * Set the port that the module is bound to.
     *
     * @param port
     */
    public void setPort(Integer port);

    /**
     * Get the root context of the module.
     *
     * @return
     */
    public String getContext();

    /**
     * Set the root context of the module.
     *
     * @param context
     */
    public void setContext(String context);

    /**
     * Get the protocol under which the module is running.
     *
     * @return
     */
    public String getProtocol();

    /**
     * Set the protocol that the module will run under.
     *
     * @param protocol
     */
    public void setProtocol(String protocol);
}
