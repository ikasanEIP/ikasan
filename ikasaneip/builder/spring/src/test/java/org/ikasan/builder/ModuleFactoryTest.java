/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */
package org.ikasan.builder;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ikasan.builder.test.dbutils.SqlOperationsRunner;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class supports the <code>ModuleFactory</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MyApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModuleFactoryTest {
    @Resource
    private Module<Flow> module;

    @Resource
    private ApplicationContext applicationContext;

    public static final String IKASAN_XA_DATASOURCE = "ikasan.xads";


    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_moduleCreation() {
        Assert.assertTrue("module name should be 'moduleName'", "moduleName".equals(module.getName()));
        Assert.assertTrue("module description should be 'moduleDescription'", "moduleDescription".equals(module.getDescription()));
        Assert.assertNotNull("module should contain a flow named 'flowName''", module.getFlow("flowName"));
        Assert.assertNotNull("module should contain a flow named 'scheduledBuilderFlow''", module.getFlow("scheduledBuilderFlow"));
    }

    private SqlOperationsRunner sqlOperationsRunner;

    @Before
    public void setupSqlOperationsRunner() {
        PlatformTransactionManager platformTransactionManager =
            applicationContext.getBean(PlatformTransactionManager.class);
        BasicDataSource dataSource = applicationContext.getBean(IKASAN_XA_DATASOURCE, BasicDataSource.class);
        sqlOperationsRunner =
            new SqlOperationsRunner(dataSource, platformTransactionManager);

    }

    @Test
    public void testStartupTypeOnFlowsSetFromApplicationDotProperties() {
        assertEquals("ID,MODULENAME,FLOWNAME,STARTUPTYPE,COMMENT\n" +
                "1,moduleName,flowName,MANUAL,Setting in properties\n" +
                "2,moduleName,scheduledBuilderFlow,AUTOMATIC,Startup Type set on Module Initialisation\n",
            sqlOperationsRunner.exportRowsToCsv("select * from STARTUPCONTROL order by ID ASC"));
    }


    @Test
    public void testWiretapTriggersSetFromApplicationDotProperties() {
        assertEquals("ID,MODULENAME,FLOWNAME,RELATIONSHIP,FLOWELEMENTNAME,JOBNAME\n" +
                "1,moduleName,flowName,after,producerAFlowElement,wiretapJob\n",
            sqlOperationsRunner.exportRowsToCsv("select * from FLOWEVENTTRIGGER order by ID ASC"));
        assertEquals("TRIGGERID,PARAMNAME,PARAMVALUE\n" +
                "1,timeToLive,601\n",
            sqlOperationsRunner.exportRowsToCsv("select * from FLOWEVENTTRIGGERPARAMETERS FLOWEVENTTRIGGERPARAMETERS order by TRIGGERID ASC"));
    }

}
