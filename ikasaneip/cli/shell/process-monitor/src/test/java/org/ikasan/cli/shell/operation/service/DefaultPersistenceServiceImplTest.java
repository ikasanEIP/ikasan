
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
package org.ikasan.cli.shell.operation.service;

import org.ikasan.cli.shell.operation.dao.ProcessPersistenceDao;
import org.ikasan.cli.shell.operation.model.IkasanProcess;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This test class supports the <code>DefaultPersistenceServiceImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
class DefaultPersistenceServiceImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /** Mock Process DAO */
    final ProcessPersistenceDao processPersistenceDao = mockery.mock(ProcessPersistenceDao.class, "mockProcessPersistenceDao");

    /** Mock Process */
    final Process process = mockery.mock(Process.class, "mockProcess");

    /** Mock ProcessHandle info */
    final ProcessHandle.Info info = mockery.mock(ProcessHandle.Info.class, "mockProcessHandleInfo");

    /** Mock IkasanProcess */
    final IkasanProcess ikasanProcess = mockery.mock(IkasanProcess.class, "mockIkasanProcess");

    /** persistenceService instance */
    PersistenceService persistenceService = new DefaultPersistenceServiceImpl(processPersistenceDao);

    @Test
    void successful_find_returns_null()
    {
        final Optional<String> user = Optional.empty();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processPersistenceDao).find("type", "name");
                will(returnValue(null));
            }
        });

        assertNull(persistenceService.find("type", "name"));
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_find_returns_ikasanProcess_no_active_process()
    {
        final Optional<String> user = Optional.of("user");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processPersistenceDao).find("type", "name");
                will(returnValue(ikasanProcess));

                exactly(1).of(ikasanProcess).getPid();
                will(returnValue(0L));
            }
        });

        persistenceService.find("type", "name");
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_persist()
    {
        final Optional<String> user = Optional.of("user");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(process).pid();
                will(returnValue(1234L));

                exactly(1).of(process).info();
                will(returnValue(info));

                exactly(1).of(info).user();
                will(returnValue(user));

                exactly(1).of(processPersistenceDao).save(with(any(IkasanProcess.class)));
            }
        });

        persistenceService.persist("type", "name", process);
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_remove()
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processPersistenceDao).delete("type", "name");
            }
        });

        persistenceService.remove("type", "name");
        mockery.assertIsSatisfied();
    }
}

