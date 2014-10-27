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
package org.ikasan.component.endpoint.ftp.endpoint;


import static org.junit.Assert.*;
import org.ikasan.component.endpoint.ftp.common.*;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 *
 * @author Ikasan Development Team
 */
public class FtpEndpointImplTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    final FtpConsumerConfiguration mockConsumerConfiguration =
            mockery.mock(FtpConsumerConfiguration.class, "mockFtpConsumerConfiguration");

    final FileTransferClient mockFileTransferClient = mockery.mock(FileTransferClient.class);

    final String clientID = "testClientId";

    final String sourceDir = "srcDir";

    final String filenamePattern = "[a-z].txt";

    final long minAge = 120;


    /**
     *  Ftp Endpoint class under test.
     */
    private FtpEndpoint uut;


    @Test
    public void testExecute() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException
    {

        // mock the mockFileTransfertClient
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
//        final ClientListEntry fileToIgnore = BaseFileTransferCommandJUnitHelper.createEntry("blah");
//        fileList.add(fileToIgnore);
//        final ClientListEntry youngFileToIgnore = BaseFileTransferCommandJUnitHelper.createEntry("b.txt", new Date());
//        fileList.add(youngFileToIgnore);
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry("a.txt");
        fileList.add(fileToDiscover);

        final BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).cd(sourceDir);
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));
                exactly(1).of(mockFileTransferClient).get(fileToDiscover);
                will(returnValue(record));


            }
        });


        uut =  new FtpEndpointImpl(mockFileTransferClient,clientID,sourceDir,
                filenamePattern,  minAge, true, true, true);


        BaseFileTransferMappedRecord output = uut.getFile();

        assertEquals(
                "command result's only entry should be the file that matches pattern", //$NON-NLS-1$
                record, output);
    }
}
