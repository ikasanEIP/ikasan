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
package org.ikasan.cli.shell.command;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

/**
 * This test class supports the <code>IkasanShell</code> class.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
class SolrCommandTest
{
    @Test
    void successful_solr_command_start_and_stop() throws IOException, JSONException
    {
        String h2SampleProcess = "java -classpath cli/shell/target/test-classes org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";

        SolrCommand command = new SolrCommand();

        // test all match
        JSONObject result = command._startSolr("sampleProcess", h2SampleProcess);

        Assert.assertTrue("running should be true", result.get("running").equals(true));
        Assert.assertTrue("type should be Solr", result.get("type").equals("Solr"));
        Assert.assertTrue("operation should be start", result.get("operation").equals("start"));
        Assert.assertNotNull("username should be not null", result.get("username"));
        Assert.assertNotNull("pid should be not null", result.get("pid"));

        result = command._stopSolr("sampleProcess", "kill -9 " + result.get("pid"));
        Assert.assertTrue("running should be false", result.get("running").equals(false));
        Assert.assertTrue("type should be Solr", result.get("type").equals("Solr"));
        Assert.assertTrue("operation should be start", result.get("operation").equals("stop"));
        Assert.assertNotNull("username should be not null", result.get("username"));
        Assert.assertNotNull("pid should be not null", result.get("pid"));
    }

    @AfterEach
    void teardown() throws IOException
    {
        FileUtils.deleteDirectory(new File("./logs"));
    }

}

