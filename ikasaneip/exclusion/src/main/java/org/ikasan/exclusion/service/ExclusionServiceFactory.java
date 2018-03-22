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
package org.ikasan.exclusion.service;

import org.ikasan.exclusion.dao.BlackListDaoFactory;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.serialiser.SerialiserFactory;

/**
 * ExclusionService Factory.
 *
 * @author Ikasan Development Team
 */
public class ExclusionServiceFactory
{
    /** blacklist DAO handle */	
    BlackListDaoFactory blackListDaoFactory;

    /** exclusionEvent DAO handle */
    ExclusionEventDao exclusionEventDao;

    /** handle to the serialiserFactory */
    SerialiserFactory serialiserFactory;

    /**
     * Constructor
     */
    public ExclusionServiceFactory(BlackListDaoFactory blackListDaoFactory, ExclusionEventDao exclusionEventDao, SerialiserFactory serialiserFactory)
    {
        this.blackListDaoFactory = blackListDaoFactory;
        if(blackListDaoFactory == null)
        {
            throw new IllegalArgumentException("blackListDaoFactory cannot be 'null'");
        }

        this.exclusionEventDao = exclusionEventDao;
        if(exclusionEventDao == null)
        {
            throw new IllegalArgumentException("exclusionEventDao cannot be 'null'");
        }

        this.serialiserFactory = serialiserFactory;
        if(serialiserFactory == null)
        {
            throw new IllegalArgumentException("serialiserFactory cannot be 'null'");
        }
    }

    /**
     * Get an instance of the ExclusionService
     * @return
     */
    public ExclusionService getExclusionService(String moduleName, String flowName)
    {
        return new ExclusionServiceDefaultImpl(moduleName, flowName, blackListDaoFactory.getBlackListDao(), exclusionEventDao, serialiserFactory.getDefaultSerialiser());
    }
}
