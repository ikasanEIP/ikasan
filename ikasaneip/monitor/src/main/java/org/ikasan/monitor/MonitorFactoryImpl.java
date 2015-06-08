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
package org.ikasan.monitor;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.monitor.Monitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ikasan default monitor factory implementation.
 *
 * @author Ikasan Development Team
 */
public class MonitorFactoryImpl implements MonitorFactory
{
    /** default executor service is a single thread executor */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final Logger logger = Logger.getLogger(MonitorFactoryImpl.class);

    /**
     * Get an instance of a monitor
     * @return
     */
    public Monitor getMonitor()
    {
        if (executorService == null || executorService.isShutdown())
        {
            throw new RuntimeException("Cannot get new Monitor after destroy method called");
        }
        Monitor monitor = new DefaultMonitorImpl(executorService);
        if(monitor instanceof Configured)
        {
            ((Configured)monitor).setConfiguration(new MonitorConfiguration());
        }

        return monitor;
    }

    /**
     * Provision the override of the default executor service
     * @param executorService
     */
    public void setExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * Convenience method to shutdown the underlying executorService
     * Subsequent calls to getMethod will cause a RuntimeException
     */
    public void destroy()
    {
        logger.info("MonitorFactory shutting down executorService");
        executorService.shutdown();
    }
}
