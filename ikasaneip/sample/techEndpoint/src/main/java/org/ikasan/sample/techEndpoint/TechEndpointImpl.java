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
package org.ikasan.sample.techEndpoint;

/**
 * Sample tech endpoint implementation
 * @author Ikasan Development Team
 */
public class TechEndpointImpl implements TechEndpoint
{
    /** tech endpoint listener implementation */
    private TechEndpointListener techEndpointListener;

    /** event count default of 3,000,000 */
    private int eventCount = 3000000;

    /**
     * Allow setting of listener
     * @param techEndpointListener
     */
    public void setListener(TechEndpointListener techEndpointListener)
    {
        this.techEndpointListener = techEndpointListener;
    }

    @Override
    public void setEventCount(int eventCount)
    {
        this.eventCount = eventCount;
    }

    /**
     * Execute the techEndpoint in a separate thread to simulate a real tech producer implementation.
     */
    @Override
    public void run()
    {
        int x = 0;
        StopWatch stopWatch = StopWatch.getInstance();

        try
        {
            stopWatch.start();

            for(;x < eventCount; x++)
            {
                this.techEndpointListener.onMessage(new Integer(x));
            }
        }
        catch(NullPointerException e)
        {
            // listener stopped listening, just dump the stacktrace
            e.printStackTrace();
        }
        finally
        {
            stopWatch.stop();
            System.out.print(stopWatch.getTime() + " for [" + eventCount + "] events.\n");
        }
    }
}