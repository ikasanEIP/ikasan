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
package org.ikasan.spec.scheduled.notification.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;

import java.util.List;

/**
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
     * Set the notifiers to which the monitor brokers notifications
     * @param notifiers
     */
    void setNotifiers(List<Notifier> notifiers);

    /**
     * Add the notifier to which the monitor brokers notifications
     * @param notifier
     */
    void addNotifier(Notifier notifier);

    /**
     * Get the notifiers registered with this monitor
     * @return
     */
    List<Notifier> getNotifiers();

    /**
     * Destroys the monitor internal, rendering this Monitor defunct.
     */
    void destroy();

    /**
     * Newly created contextInstance registers itself to Monitor .
     */
    void register(ContextInstance contextInstance);

    /**
     * Remove the contextInstance from the Monitor .
     */
    void unregister(ContextInstance contextInstance);

}
