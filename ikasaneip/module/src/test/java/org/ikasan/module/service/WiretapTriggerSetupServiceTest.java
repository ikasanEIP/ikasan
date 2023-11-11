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

import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WiretapTriggerSetupServiceTest {

    private JobAwareFlowEventListener wiretapTriggerService = Mockito.mock(JobAwareFlowEventListener.class);

    private ArgumentCaptor<Trigger> triggerArgumentCaptor = ArgumentCaptor.forClass(Trigger.class);

    private WiretapTriggerConfiguration triggerConfiguration;

    private WiretapTriggerSetupService wiretapTriggerSetupService;


    @BeforeEach
    void setup() {
        triggerConfiguration = new WiretapTriggerConfiguration();
        triggerConfiguration.setAction(WiretapTriggerAction.INSERT);
        triggerConfiguration.setComponentName("componentName");
        triggerConfiguration.setFlowName("flowName");
        triggerConfiguration.setRelationship("before");
        triggerConfiguration.setTimeToLive("600");
        WiretapTriggerSetupServiceConfiguration configuration = new WiretapTriggerSetupServiceConfiguration();
        configuration.setTriggers(Arrays.asList(triggerConfiguration));
        wiretapTriggerSetupService = new WiretapTriggerSetupService(configuration,
            wiretapTriggerService);
    }

    @Test
    void testInsertTrigger() {
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService).addDynamicTrigger(triggerArgumentCaptor.capture());
        assertEquals("moduleName", triggerArgumentCaptor.getValue().getModuleName());
        assertEquals("flowName", triggerArgumentCaptor.getValue().getFlowName());
        assertEquals("componentName", triggerArgumentCaptor.getValue().getFlowElementName());
        assertEquals("wiretapJob", triggerArgumentCaptor.getValue().getJobName());
        assertEquals(TriggerRelationship.BEFORE, triggerArgumentCaptor.getValue().getRelationship());
        assertEquals("600", triggerArgumentCaptor.getValue().getParams().get("timeToLive"));
    }


    @Test
    void testInsertTriggerThatAlreadyExistsInDb() {
        Trigger existingTrigger = Mockito.mock(Trigger.class);
        when(existingTrigger.getJobName()).thenReturn("wiretapJob");
        when(wiretapTriggerService.getTriggers("moduleName", "flowName",
            TriggerRelationship.BEFORE, "componentName")).thenReturn(Arrays.asList(existingTrigger));
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService, Mockito.times(0)).addDynamicTrigger(any(Trigger.class));
    }

    @Test
    void testUpdateTriggerThatDoesntExistInDb() {
        triggerConfiguration.setAction(WiretapTriggerAction.UPDATE);
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService, Mockito.times(0)).addDynamicTrigger(any(Trigger.class));
    }

    @Test
    void testUpdateTrigger() {
        triggerConfiguration.setAction(WiretapTriggerAction.UPDATE);
        Trigger existingTrigger = existingWiretapTrigger();
        when(wiretapTriggerService.getTriggers("moduleName", "flowName",
            TriggerRelationship.BEFORE, "componentName")).thenReturn(Arrays.asList(existingTrigger));
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService).addDynamicTrigger(triggerArgumentCaptor.capture());
        assertEquals("600", triggerArgumentCaptor.getValue().getParams().get("timeToLive"));
    }

    @Test
    void testDeleteTriggerThatDoesntExistInDb() {
        triggerConfiguration.setAction(WiretapTriggerAction.DELETE);
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService, Mockito.times(0)).deleteDynamicTrigger(any(Long.class));
    }

    @Test
    void testDeleteTrigger() {
        triggerConfiguration.setAction(WiretapTriggerAction.DELETE);
        Trigger existingTrigger = existingWiretapTrigger();
        when(wiretapTriggerService.getTriggers("moduleName", "flowName",
            TriggerRelationship.BEFORE, "componentName")).thenReturn(Arrays.asList(existingTrigger));
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService).deleteDynamicTrigger(1L);
    }

    @Test
    void deleteAll(){
        WiretapTriggerSetupServiceConfiguration configuration = new WiretapTriggerSetupServiceConfiguration();
        configuration.setDeleteAllTriggers(true);
        wiretapTriggerSetupService = new WiretapTriggerSetupService(configuration, wiretapTriggerService);
        Trigger existingTrigger = existingWiretapTrigger();
        TestTrigger nonWiretapTrigger = new TestTrigger(existingTrigger.getModuleName(), existingTrigger.getFlowName(),
            existingTrigger.getRelationship().getDescription(),"loggingJob", "component",
            new LinkedHashMap<>());
        nonWiretapTrigger.setId(2L);
        when(wiretapTriggerService.getTriggers()).thenReturn(List.of(existingTrigger,nonWiretapTrigger));
        wiretapTriggerSetupService.setup("moduleName");
        verify(wiretapTriggerService, Mockito.times(1)).deleteDynamicTrigger(1L);
        verify(wiretapTriggerService, Mockito.times(0)).deleteDynamicTrigger(2L);
    }

    @Test
    void testRestClientExceptionCallingAddDynamicTrigger(){
        triggerConfiguration.setAction(WiretapTriggerAction.INSERT);
        doThrow(new RestClientException("test rest client exception")).when(
            wiretapTriggerService).addDynamicTrigger(any(TriggerImpl.class));
        wiretapTriggerSetupService.setup("moduleName");
    }


    private TriggerImpl existingWiretapTrigger() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("timeToLive", "300");
        TestTrigger trigger = new TestTrigger("moduleName", "flowName",
            TriggerRelationship.BEFORE.getDescription(), "wiretapJob", "componentName",
            params);
        trigger.setId(1L);
        return trigger;
    }

    /**
     * Need this as setId is private in TriggerImpl
     */
    public static class TestTrigger extends TriggerImpl {

        private String flowElementName;
        private String flowName;
        private Long id;

        private String jobName;
        private String moduleName;
        private Map<String, String> params;
        private TriggerRelationship relationship;

        public TestTrigger(String moduleName, String flowName, String relationshipDescription,
                           String jobName, String flowElementName, Map<String, String> params) {
            super(moduleName, flowName, relationshipDescription, jobName, flowElementName, params);
            this.moduleName = moduleName;
            this.flowName = flowName;
            this.jobName = jobName;
            this.params = params;
            this.relationship = TriggerRelationship.get(relationshipDescription);
        }

        @Override
        public String getFlowElementName() {
            return flowElementName;
        }

        public void setFlowElementName(String flowElementName) {
            this.flowElementName = flowElementName;
        }

        @Override
        public String getFlowName() {
            return flowName;
        }

        public void setFlowName(String flowName) {
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
        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        @Override
        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public TriggerRelationship getRelationship() {
            return relationship;
        }

        public void setRelationship(TriggerRelationship relationship) {
            this.relationship = relationship;
        }
    }
}