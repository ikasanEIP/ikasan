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
package org.ikasan.cli.shell.operation.model;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * This test class supports the <code>ProcessType</code> class.
 * 
 * @author Ikasan Development Team
 */
class ProcessTypeTest
{
    @Test
    void successful_h2_instance()
    {
        ProcessType h2Instance = ProcessType.getH2Instance();
        Assert.assertTrue(h2Instance.getName().equals("H2"));
        Assert.assertTrue(h2Instance.isPersist());
        Assert.assertTrue(h2Instance.getOutputLog().equals("logs/h2.log"));
        Assert.assertTrue(h2Instance.getErrorLog().equals("logs/h2.log"));
        Assert.assertTrue(h2Instance.getCommandSignature().equals("org.h2.tools.Server"));
    }

    @Test
    void successful_module_instance()
    {
        ProcessType moduleInstance = ProcessType.getModuleInstance();
        Assert.assertTrue(moduleInstance.getName().equals("Module"));
        Assert.assertTrue(moduleInstance.isPersist());
        Assert.assertTrue(moduleInstance.getOutputLog().equals("logs/application.log"));
        Assert.assertTrue(moduleInstance.getErrorLog().equals("logs/application.log"));
        Assert.assertTrue(moduleInstance.getCommandSignature().equals("spring.jta.logDir"));
    }

    @Test
    void successful_generic_instance()
    {
        ProcessType genericInstance = ProcessType.getGenericInstance();
        Assert.assertTrue(genericInstance.getName().equals(""));
        Assert.assertTrue(!genericInstance.isPersist());
        Assert.assertNull(genericInstance.getOutputLog());
        Assert.assertNull(genericInstance.getErrorLog());
        Assert.assertNull(genericInstance.getCommandSignature());
    }
}

