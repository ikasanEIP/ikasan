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
package org.ikasan.module.dao;

import liquibase.Liquibase;
import org.ikasan.module.IkasanModuleAutoConfiguration;
import org.ikasan.module.IkasanModuleTestAutoConfiguration;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IkasanModuleAutoConfiguration.class, IkasanModuleTestAutoConfiguration.class})
public class HibernateStartupControlDaoTest
{
	@Autowired
	private StartupControlDao startupControlDao;

    @MockBean(name = "configurationService")
    private ConfigurationService configurationService;

    @MockBean(name = "liquibase")
    private Liquibase liquibase;

    @MockBean(name = "moduleMetadataDashboardRestService")
    private DashboardRestService moduleMetadataDashboardRestService;

    @MockBean(name = "configurationMetadataDashboardRestService")
    private DashboardRestService configurationMetadataDashboardRestService;

    @MockBean(name = "wiretapFlowEventListener")
    private JobAwareFlowEventListener wiretapFlowEventListener;

    @MockBean(name = "housekeepingSchedulerService")
    private HousekeepingSchedulerService housekeepingSchedulerService;

    @MockBean(name = "harvestingSchedulerService")
    private HarvestingSchedulerService harvestingSchedulerService;

    @MockBean(name = "systemEventService")
    private SystemEventService systemEventService;

    @Test
    @DirtiesContext
    public void test_save_success() {
        StartupControlImpl startupControl = new StartupControlImpl("moduleName", "flowName");
        startupControl.setStartupType(StartupType.MANUAL);
        startupControl.setComment("comment");
        this.startupControlDao.save(startupControl);

        StartupControl found = startupControlDao.getStartupControl("moduleName", "flowName");

        Assert.assertNotNull(found);
        Assert.assertEquals("moduleName", found.getModuleName());
        Assert.assertEquals("flowName", found.getFlowName());
        Assert.assertEquals(StartupType.MANUAL, found.getStartupType());
        Assert.assertEquals("comment", found.getComment());

        found.setStartupType(StartupType.AUTOMATIC);
        this.startupControlDao.save(found);

        found = startupControlDao.getStartupControl("moduleName", "flowName");

        Assert.assertNotNull(found);
        Assert.assertEquals("moduleName", found.getModuleName());
        Assert.assertEquals("flowName", found.getFlowName());
        Assert.assertEquals(StartupType.AUTOMATIC, found.getStartupType());
        Assert.assertEquals("comment", found.getComment());

        found.setStartupType(StartupType.DISABLED);
        this.startupControlDao.save(found);

        found = startupControlDao.getStartupControl("moduleName", "flowName");

        Assert.assertNotNull(found);
        Assert.assertEquals("moduleName", found.getModuleName());
        Assert.assertEquals("flowName", found.getFlowName());
        Assert.assertEquals(StartupType.DISABLED, found.getStartupType());
        Assert.assertEquals("comment", found.getComment());
    }

    @Test
    @DirtiesContext
    public void test_bulk_query_success() {
        IntStream.range(0, 10).forEach(i -> {
            StartupControlImpl startupControl = new StartupControlImpl("moduleName", "flowName"+i);
            startupControl.setStartupType(StartupType.MANUAL);
            startupControl.setComment("comment");
            this.startupControlDao.save(startupControl);
        });

        Assert.assertEquals(10, this.startupControlDao.getStartupControls("moduleName").size());
    }

    @Test
    @DirtiesContext
    public void test_delete_success() {
        IntStream.range(0, 10).forEach(i -> {
            StartupControlImpl startupControl = new StartupControlImpl("moduleName", "flowName"+i);
            startupControl.setStartupType(StartupType.MANUAL);
            startupControl.setComment("comment");
            this.startupControlDao.save(startupControl);
        });

        List<StartupControl> startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(10, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(9, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(8, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(7, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(6, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(5, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(4, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(3, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(2, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(1, startupControls.size());

        this.startupControlDao.delete(startupControls.get(0));

        startupControls = this.startupControlDao.getStartupControls("moduleName");
        Assert.assertEquals(0, startupControls.size());
    }

}
