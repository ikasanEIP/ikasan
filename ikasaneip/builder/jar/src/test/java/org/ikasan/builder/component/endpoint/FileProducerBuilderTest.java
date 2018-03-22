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
package org.ikasan.builder.component.endpoint;

import org.ikasan.component.endpoint.filesystem.producer.FileProducer;
import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>FileProducerBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class FileProducerBuilderTest {

    @Test
    public void test_fileproducerbuilder_build_invalid_properties() {

        new FileProducerBuilderImpl().build();
    }

    @Test
    public void test_fileproducerbuilder_build_success() {

        FileProducerBuilder fileProducerBuilder = new FileProducerBuilderImpl();

        Producer fileProducer = fileProducerBuilder.setConfiguredResourceId("configuredResourceId")
                .setEncoding("UTF-16")
                .setFilename("test.txt")
                .setLineEnding("\n")
                .setTempFilename("tempy.tmp")
                .setUseTempFile(true)
                .setWriteChecksum(true)
                .setOverwrite(true)
                .build();

        assertTrue("instance should be a fileProducer", fileProducer instanceof FileProducer);

        assertTrue("configuredResourceId should be 'configuredResourceId'",
                "configuredResourceId".equals(((ConfiguredResource)fileProducer).getConfiguredResourceId()));

        FileProducerConfiguration configuration = (
                (ConfiguredResource< FileProducerConfiguration>) fileProducer).getConfiguration();

        assertEquals("encoding should be 'UTF-16'", "UTF-16", configuration.getEncoding());
        assertEquals("filename should be 'test.txt'", "test.txt", configuration.getFilename());
        assertEquals("lineEnding should be '\\n'", "\n", configuration.getLineEnding());
        assertEquals("tempFilename should be 'tempy.tmp'", "tempy.tmp", configuration.getTempFilename());
        assertTrue("useTempFile should be 'true'", configuration.isUseTempFile());
        assertTrue("overwrite should be 'true'", configuration.isOverwrite());
        assertTrue("writeChecksum should be 'true'", configuration.isWriteChecksum());
    }


}
