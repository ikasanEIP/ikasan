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

package org.ikasan.module.service;

import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BulkStartupTypeSetupServiceTest {


    private StartupControlDao startupControlDao = Mockito.mock(StartupControlDao.class);


    @Test
    public void testInsertStartupTypeWhenNotPreviouslySaved() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDefaultStartupType(StartupType.AUTOMATIC);
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);
        TestStartupControl testStartupControl = new TestStartupControl("moduleName", "flowName");
        testStartupControl.setStartupType(StartupType.MANUAL);
        testStartupControl.setComment("starting comment");
        StartupControl startupControl = bulkStartupTypeSetupService.setup(testStartupControl);
        assertEquals(StartupType.AUTOMATIC, startupControl.getStartupType());
        assertEquals("moduleName", startupControl.getModuleName());
        assertEquals("flowName", startupControl.getFlowName());
        assertEquals("Startup Type set on Module Initialisation", startupControl.getComment());
        verify(startupControlDao).save(any(StartupControlImpl.class));
    }

    @Test
    public void testInsertStartupTypeUsingFlowStartupConfiguration() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDefaultStartupType(StartupType.AUTOMATIC);
        FlowStartupTypeConfiguration flowStartupTypeConfiguration = new FlowStartupTypeConfiguration();
        flowStartupTypeConfiguration.setStartupType(StartupType.DISABLED);
        flowStartupTypeConfiguration.setFlowName("flowName");
        flowStartupTypeConfiguration.setComment("flow specific comment");

        configuration.setFlowStartupTypes(Arrays.asList( flowStartupTypeConfiguration));
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);
        TestStartupControl testStartupControl = new TestStartupControl("moduleName", "flowName");
        testStartupControl.setStartupType(StartupType.MANUAL);
        testStartupControl.setComment("starting comment");
        StartupControl startupControl = bulkStartupTypeSetupService.setup(testStartupControl);
        assertEquals(StartupType.DISABLED, startupControl.getStartupType());
        assertEquals("moduleName", startupControl.getModuleName());
        assertEquals("flowName", startupControl.getFlowName());
        assertEquals("flow specific comment", startupControl.getComment());
        verify(startupControlDao).save(any(StartupControlImpl.class));
    }

    @Test
    public void testInsertNotAllowedWithFlowsNotToSet() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDefaultStartupType(StartupType.AUTOMATIC);
        configuration.setFlowsNotToSet(Arrays.asList("flowName"));
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);
        TestStartupControl testStartupControl = new TestStartupControl("moduleName", "flowName");
        testStartupControl.setStartupType(StartupType.MANUAL);
        testStartupControl.setComment("starting comment");
        StartupControl startupControl = bulkStartupTypeSetupService.setup(testStartupControl);
        assertEquals(StartupType.MANUAL, startupControl.getStartupType());
        assertEquals("moduleName", startupControl.getModuleName());
        assertEquals("flowName", startupControl.getFlowName());
        assertEquals("starting comment", startupControl.getComment());
        verify(startupControlDao,Mockito.times(0)).save(any(StartupControlImpl.class));
    }

    @Test
    public void testUpdateStartupTypeWhenPreviouslySavedIsNotAllowed() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDefaultStartupType(StartupType.AUTOMATIC);
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);
        TestStartupControl testStartupControl = new TestStartupControl("moduleName", "flowName");
        testStartupControl.setStartupType(StartupType.MANUAL);
        testStartupControl.setComment("starting comment");
        testStartupControl.setId(1L);
        StartupControl startupControl = bulkStartupTypeSetupService.setup(testStartupControl);
        assertEquals(StartupType.MANUAL, startupControl.getStartupType());
        assertEquals("moduleName", startupControl.getModuleName());
        assertEquals("flowName", startupControl.getFlowName());
        assertEquals("starting comment", startupControl.getComment());
        verify(startupControlDao,Mockito.times(0)).save(any(StartupControlImpl.class));
    }

    @Test
    public void testUpdateStartupTypeWhenPreviouslySavedIsAllowed() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDefaultStartupType(StartupType.AUTOMATIC);
        configuration.setAllowDbOverwrite(true); // This means are allowed to to update startup type if saved previously
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);

        TestStartupControl startupControlImpl = new TestStartupControl("moduleName", "flowName");
        startupControlImpl.setStartupType(StartupType.MANUAL);
        startupControlImpl.setComment("starting comment");
        startupControlImpl.setId(1L);
        StartupControl startupControl = bulkStartupTypeSetupService.setup(startupControlImpl);
        assertEquals(StartupType.AUTOMATIC, startupControl.getStartupType());
        assertEquals("moduleName", startupControl.getModuleName());
        assertEquals("flowName", startupControl.getFlowName());
        assertEquals("Startup Type set on Module Initialisation", startupControl.getComment());
        verify(startupControlDao).save(any(StartupControlImpl.class));
    }

    @Test
    public void testDeleteAllOnlyIfConfigured() {
        BulkStartupTypeSetupServiceConfiguration configuration = new BulkStartupTypeSetupServiceConfiguration();
        configuration.setDeleteAll(true);
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(configuration,
            startupControlDao);

        TestStartupControl startupControlImpl = new TestStartupControl("moduleName", "flowName");
        startupControlImpl.setStartupType(StartupType.MANUAL);
        startupControlImpl.setComment("starting comment");
        startupControlImpl.setId(1L);

        when(startupControlDao.getStartupControls("moduleName"))
            .thenReturn(Arrays.asList(startupControlImpl));
        bulkStartupTypeSetupService.deleteAllOnlyIfConfigured("moduleName");
        verify(startupControlDao).delete(startupControlImpl);
    }


    @Test
    public void testNoConfigurationSet() {
        BulkStartupTypeSetupService bulkStartupTypeSetupService = new BulkStartupTypeSetupService(null,
            startupControlDao);
        TestStartupControl testStartupControl = new TestStartupControl("moduleName", "flowName");
        testStartupControl.setStartupType(StartupType.MANUAL);
        testStartupControl.setComment("starting comment");
        StartupControl startupControl = bulkStartupTypeSetupService.setup(testStartupControl);
    }



    public static class TestStartupControl extends StartupControlImpl {
        private Long id;

        private String moduleName;

        private String flowName;

        private StartupType startupType;

        private String comment;

        /**
         * Constructor
         *
         * @param moduleName - Name of the module with which the target Flow is
         *                   associated
         * @param flowName   - Name of the target Flow
         */
        public TestStartupControl(String moduleName, String flowName) {
            super(moduleName, flowName);
            this.moduleName = moduleName;
            this.flowName = flowName;
        }

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        @Override
        public String getFlowName() {
            return flowName;
        }

        public void setFlowName(String flowName) {
            this.flowName = flowName;
        }

        @Override
        public StartupType getStartupType() {
            return startupType;
        }

        @Override
        public void setStartupType(StartupType startupType) {
            this.startupType = startupType;
        }

        @Override
        public String getComment() {
            return comment;
        }

        @Override
        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}