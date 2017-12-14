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
package org.ikasan.spec.replay;

import java.util.List;


/**
 * ReplayService contract.
 * 
 * @author Ikasan Development Team
 */
public interface ReplayService<EVENT, AUDIT_EVENT, REPLAY_RESPONSE, BULK_REPLAY_RESPONSE>
{
	/**
	 * Add a replay listener.
	 * 
	 * @param listener
	 */
	public void addReplayListener(ReplayListener<AUDIT_EVENT> listener);

    /**
     * Entry point for replay of a list of events.
     * 
     * @param targetServer
     * @param events
     * @param authUser
     * @param authPassword
     * @param user
     */
    public BULK_REPLAY_RESPONSE replay(String targetServer, List<EVENT> events, String authUser, String authPassword, String user, String replayReason);


    /**
     * Entry point for replay of an individual event.
     *
     * @param targetServer
     * @param event
     * @param authUser
     * @param authPassword
     * @param user
     */
    public REPLAY_RESPONSE replay(String targetServer, EVENT event, String authUser, String authPassword, String user, String replayReason);
    
    /**
     * Method to cancel the replay.
     */
    public void cancel();

    /**
     * Has the replay been cancelled?
     * 
     * @return
     */
    public boolean isCancelled();
}
