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
package org.ikasan.spec.monitor;

import java.util.List;

/**
 * Monitor contract for the subject notification to the monitor
 * 
 * @author Ikasan Development Team
 */
public interface Monitor<NOTIFICATION>
{
    /**
     * Invoke the monitor with the given notification
     * @param notification
     */
    void invoke(NOTIFICATION notification);

    /**
     * Set the name of this monitor
     * @param monitorName
     */
    void setName(String monitorName);

    /**
     * Get the name of this monitor
     * @return
     */
    String getName();

    /**
     * Set the runtime environment name this monitor is associated with
     * @param environmentName
     */
    void setEnvironment(String environmentName);

    /**
     * Get the runtime environment name this monitor is associated with
     * @return
     */
    String getEnvironment();

    /**
     * Set the notifiers to which the monitor brokers notifications
     * @param notifiers
     */
    void setNotifiers(List<Notifier> notifiers);

    /**
     * Get the notifiers registered with this monitor
     * @return
     */
    List<Notifier> getNotifiers();

    /**
     * Destroys the monitor internal, rendering this Monitor defunct.
     */
    void destroy();
}
