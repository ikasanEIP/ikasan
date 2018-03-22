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
package org.ikasan.replay.service;

import org.ikasan.spec.replay.ReplayDao;
import org.ikasan.replay.model.HibernateReplayEvent;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.serialiser.SerialiserFactory;

/**
 * Replay record service implementation.
 * 
 * @author Ikasan Development Team
 * 
 */
public class ReplayRecordServiceImpl implements ReplayRecordService<FlowEvent<String,?>>
{
	/** need a serialiser to serialise the incoming event payload of T */
    private SerialiserFactory serialiserFactory;
    
    /** the underlying dao **/
    private ReplayDao replayDao;
     
    /**
     * Constructor
     * 
     * @param serialiserFactory
     * @param replayDao
     */
	public ReplayRecordServiceImpl(SerialiserFactory serialiserFactory,
			ReplayDao replayDao) 
	{
		super();
		this.serialiserFactory = serialiserFactory;
		if(serialiserFactory == null)
		{
			throw new IllegalArgumentException("SerialiserFactory cannot be null!");
		}
		this.replayDao = replayDao;
		if(this.replayDao == null)
		{
			throw new IllegalArgumentException("ReplayDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayRecordService#record(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void record(FlowEvent<String,?> event, String moduleName, String flowName, int timeToLiveDays) 
	{
        byte[] bytes = (byte[])this.serialiserFactory.getDefaultSerialiser().serialise(event.getPayload());

		String eventAsString = null;

		if(event.getPayload() != null)
		{
			eventAsString = event.getPayload().toString();
		}

        HibernateReplayEvent replayEvent = new HibernateReplayEvent(event.getIdentifier(), bytes
				, eventAsString, moduleName, flowName, timeToLiveDays);
        
        this.replayDao.saveOrUpdate(replayEvent);
	}
}
