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
package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.operation.dao.KryoProcessPersistenceImpl;
import org.ikasan.cli.shell.operation.model.ProcessType;
import org.ikasan.cli.shell.operation.service.DefaultPersistenceServiceImpl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;

/**
 * Contract for the Operation
 *
 * @author Ikasan Development Team
 */
public interface Operation
{
    String defaultPidDirectory = "." + FileSystems.getDefault().getSeparator() + "pid";

    /**
     * Get default instance of an Operation.
     *
     * @return Operation
     */
    static Operation getInstance()
    {
        return new DefaultOperationImpl( new DefaultPersistenceServiceImpl( new KryoProcessPersistenceImpl(defaultPidDirectory) ) );
    }

    /**
     * Method to start a new process.
     *
     * @param processType
     * @param name
     * @throws IOException
     */
    Process start(ProcessType processType, List<String> commands, String name) throws IOException;

    /**
     *
     * @param processType
     * @param name
     * @param username
     * @return
     */
    List<ProcessHandle> getProcessHandles(ProcessType processType, String name, String username);

    /**
     * Method to gracefully shutdown the underlying process.
     *
     * @param processType
     * @param name
     * @param username
     * @throws IOException
     */
    void stop(ProcessType processType, String name, String username, int shutdownTimeoutSeconds) throws IOException;

    /**
     * Method to kill the underlying process.
     *
     * @param processType
     * @param name
     * @param username
     * @throws IOException
     */
    void kill(ProcessType processType, String name, String username) throws IOException;

}