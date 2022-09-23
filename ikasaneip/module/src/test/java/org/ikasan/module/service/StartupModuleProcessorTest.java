package org.ikasan.module.service;

import org.ikasan.module.SimpleModule;
import org.ikasan.module.StartupModuleConfiguration;
import org.ikasan.module.WiretapTriggerConfiguration;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.ikasan.module.service.StartupModuleProcessor.WIRETAP_JOB;
import static org.mockito.Mockito.*;

public class StartupModuleProcessorTest  {


    /**
     * Test a module with three flows, and configuration has defaultFlowStartupType = AUTOMATIC and a
     * flowNameToStartupMap with a single entry "manual Flow" -> "MANUAL". The first flow flowOne has
     * already got a startupControl saved against it so wont expect it to be changed. However flowTwo
     * should have its startupControl saved with the default startupType 'AUTOMATIC' and manualFlow
     * should use the explicit flowNameToStartupMap and have its startupType saved as MANUAL.
     */
    @Test
    public void testSaveUnsavedStartupTypes(){
        StartupModuleConfiguration startupModuleConfiguration = new StartupModuleConfiguration();
        startupModuleConfiguration.setDefaultFlowStartupType("AUTOMATIC");
        startupModuleConfiguration.setFlowNameToStartupTypeMap(Map.of("manualFlow", "MANUAL"));
        StartupControlDao startupControlDao = Mockito.mock(StartupControlDao.class);
        Flow flowOne = Mockito.mock(Flow.class);
        when(flowOne.getName()).thenReturn("flowOne");
        Flow flowTwo = Mockito.mock(Flow.class);
        when(flowTwo.getName()).thenReturn("flowTwo");
        Flow manualFlow = Mockito.mock(Flow.class);
        when(manualFlow.getName()).thenReturn("manualFlow");

        // flowOne already has saved startup type so should NOT be changed to "AUTOMATIC"
        StartupControl flowOneExistingInDbStartupControl
            = new StartupControlImpl("simple-module","flowOne");
        flowOneExistingInDbStartupControl.setStartupType(StartupType.MANUAL);
        when(startupControlDao.getStartupControls("simpleModule")).thenReturn(
            Arrays.asList(flowOneExistingInDbStartupControl));


        StartupModuleProcessor startupModuleProcessor = new StartupModuleProcessor(startupModuleConfiguration,
            startupControlDao, null);
        List<Flow> flows = Arrays.asList(flowOne, flowTwo, manualFlow);
        SimpleModule simpleModule = new SimpleModule("simpleModule", flows);

        StartupControl expectedFlowTwoStartupControl = new StartupControlImpl("simpleModule",
            "flowTwo");
        expectedFlowTwoStartupControl.setStartupType(StartupType.AUTOMATIC);
        StartupControl expectedManualFlowStartupControl = new StartupControlImpl("simpleModule",
            "manualFlow");
        expectedManualFlowStartupControl.setStartupType(StartupType.MANUAL);
        startupModuleProcessor.run(simpleModule);
        verify(startupControlDao, times(1)).getStartupControls("simpleModule");

        // verify flow two has startup type changed to the configs defaultFlowStartupType "AUTOMATIC" and is saved
        verify(startupControlDao, times(1))
            .save(argThat(new StartupControlMatcher(expectedFlowTwoStartupControl)));
        // verify manualFlow has startup type changed to the configs flowNameToStartupTypeMap['manualFlow'] = 'MANUAL'
        // and is saved
        verify(startupControlDao, times(1))
            .save(argThat(new StartupControlMatcher(expectedManualFlowStartupControl)));

        // flow one wont be changed and saved because it already has a saved startup type
        // this processor CANNOT change any previously saved configurations
        Mockito.verifyNoMoreInteractions(startupControlDao);
    }

    /**
     * Same test but with configuration option to delete all previously saved startup types set to true. Now
     * would expect flowOne to have its startupType saved as the default 'AUTOMATIC'
     */
    @Test
    public void testSaveUnsavedStartupTypesWithDeleteAllPreviouslySavedStartupTypes(){
        StartupModuleConfiguration startupModuleConfiguration = new StartupModuleConfiguration();
        startupModuleConfiguration.setDefaultFlowStartupType("AUTOMATIC");
        startupModuleConfiguration.setFlowNameToStartupTypeMap(Map.of("manualFlow", "MANUAL"));
        // DELETE ALL PREVIOUSLY SAVED STARTUP TYPES
        startupModuleConfiguration.setDeleteAllPreviouslySavedStartupTypes(true);
        StartupControlDao startupControlDao = Mockito.mock(StartupControlDao.class);
        Flow flowOne = Mockito.mock(Flow.class);
        when(flowOne.getName()).thenReturn("flowOne");
        Flow flowTwo = Mockito.mock(Flow.class);
        when(flowTwo.getName()).thenReturn("flowTwo");
        Flow manualFlow = Mockito.mock(Flow.class);
        when(manualFlow.getName()).thenReturn("manualFlow");

        // flowOne already has saved startup type but this will get deleted
        // due to deleteAllPreviouslySavedStartupTypes = TRUE
        StartupControl flowOneExistingInDbStartupControl
            = new StartupControlImpl("simple-module","flowOne");
        flowOneExistingInDbStartupControl.setStartupType(StartupType.MANUAL);
        when(startupControlDao.getStartupControls("simpleModule")).thenReturn(
            Arrays.asList(flowOneExistingInDbStartupControl));


        StartupModuleProcessor startupModuleProcessor = new StartupModuleProcessor(startupModuleConfiguration,
            startupControlDao, null);
        List<Flow> flows = Arrays.asList(flowOne, flowTwo, manualFlow);
        SimpleModule simpleModule = new SimpleModule("simpleModule", flows);

        StartupControl expectedFlowOneStartupControl = new StartupControlImpl("simpleModule",
            "flowOne");
        expectedFlowOneStartupControl.setStartupType(StartupType.AUTOMATIC);
        StartupControl expectedFlowTwoStartupControl = new StartupControlImpl("simpleModule",
            "flowTwo");
        expectedFlowTwoStartupControl.setStartupType(StartupType.AUTOMATIC);
        StartupControl expectedManualFlowStartupControl = new StartupControlImpl("simpleModule",
            "manualFlow");
        expectedManualFlowStartupControl.setStartupType(StartupType.MANUAL);
        startupModuleProcessor.run(simpleModule);
        verify(startupControlDao, times(1)).getStartupControls("simpleModule");

        // verify flow one will have its startup type DELETED
        verify(startupControlDao, times(1)).delete(flowOneExistingInDbStartupControl);
        // verify flow one NOW DOES have startup type changed to the configs defaultFlowStartupType "AUTOMATIC" and is saved
        // flow one NO LONGER WILL have a previously saved startupType due to configuration option
        // deleteAllPreviouslySavedStartupTypes set to TRUE
        verify(startupControlDao, times(1))
            .save(argThat(new StartupControlMatcher(expectedFlowOneStartupControl)));
        // verify flow two has startup type changed to the configs defaultFlowStartupType "AUTOMATIC" and is saved
        verify(startupControlDao, times(1))
            .save(argThat(new StartupControlMatcher(expectedFlowTwoStartupControl)));
        // verify manualFlow has startup type changed to the configs flowNameToStartupTypeMap['flowOne'] = 'MANUAL'
        // and is saved
        verify(startupControlDao, times(1))
            .save(argThat(new StartupControlMatcher(expectedManualFlowStartupControl)));

        Mockito.verifyNoMoreInteractions(startupControlDao);
    }

    /**
     * Testing adding two wiretaps one which exists already on flow one, jms consumer, the other doesnt on flow one
     * jms producer should result in only the single jms producer wiretap being added
     */
    @Test
    public void testSaveUnsavedWiretaps() {
        JobAwareFlowEventListener jobAwareFlowEventListener = Mockito.mock(JobAwareFlowEventListener.class);
        Map<String,String> params = new LinkedHashMap<>();
        params.put("timeToLive", "600000");
        Trigger existingFlowOneAfterConsumerWiretap = new TriggerImpl("simpleModule", "flowOne", "AFTER",
            WIRETAP_JOB, "jmsConsumer", params);
        List<Trigger>preExistingTriggers = Arrays.asList(existingFlowOneAfterConsumerWiretap);
        when(jobAwareFlowEventListener.getTriggers()).thenReturn(preExistingTriggers);
        Flow flowOne = Mockito.mock(Flow.class);
        when(flowOne.getName()).thenReturn("flowOne");
        Flow flowTwo = Mockito.mock(Flow.class);
        when(flowTwo.getName()).thenReturn("flowTwo");
        Flow manualFlow = Mockito.mock(Flow.class);
        when(manualFlow.getName()).thenReturn("manualFlow");
        List<Flow> flows = Arrays.asList(flowOne, flowTwo, manualFlow);
        SimpleModule simpleModule = new SimpleModule("simpleModule", flows);


        Map<String, WiretapTriggerConfiguration> wiretapsMap = new LinkedHashMap<>();
        wiretapsMap.put("flowOneAfterConsumerWiretap", new WiretapTriggerConfiguration("flowOne",
                "jmsConsumer", "AFTER", "600"));
        wiretapsMap.put("flowOneBeforeProducerWiretap", new WiretapTriggerConfiguration("flowOne",
            "jmsProducer", "BEFORE", "600"));

        StartupModuleConfiguration startupModuleConfiguration = new StartupModuleConfiguration();
        startupModuleConfiguration.setWiretaps(wiretapsMap);

        StartupModuleProcessor startupModuleProcessor = new StartupModuleProcessor(startupModuleConfiguration,
            null, jobAwareFlowEventListener);

        Map<String,String> expectedFlowOneBeforeProducerWiretapMap = new LinkedHashMap<>();
        expectedFlowOneBeforeProducerWiretapMap.put("timeToLive", "600000");
        TriggerImpl expectedFlowOneBeforeProducerWiretap = new TriggerImpl("simpleModule", "flowOne",
            "BEFORE",WIRETAP_JOB, "jmsProducer",
            expectedFlowOneBeforeProducerWiretapMap);

        startupModuleProcessor.run(simpleModule);
        verify(jobAwareFlowEventListener, times(1)).getTriggers();
        verify(jobAwareFlowEventListener, times(1)).addDynamicTrigger(argThat(
            new TriggerMatcher(expectedFlowOneBeforeProducerWiretap) ));
        Mockito.verifyNoMoreInteractions(jobAwareFlowEventListener);
    }

    /**
     * Same as previous but also deletes all wiretaps via a configuration option first so in this case both
     * wiretaps should then be saved after the delete
     */
    @Test
    public void testSaveUnsavedWiretapsAfterAllWiretapsDeleted() {
        JobAwareFlowEventListener jobAwareFlowEventListener = Mockito.mock(JobAwareFlowEventListener.class);
        Map<String,String> params = new LinkedHashMap<>();
        params.put("timeToLive", "600000");
        Trigger existingFlowOneAfterConsumerWiretap = new TriggerImpl("simpleModule", "flowOne", "AFTER",
            WIRETAP_JOB, "jmsConsumer", params);

        // first call gets triggers to delete, then second call there are no triggers so will return null
        when(jobAwareFlowEventListener.getTriggers()).thenReturn(
            Arrays.asList(existingFlowOneAfterConsumerWiretap), null);

        Flow flowOne = Mockito.mock(Flow.class);
        when(flowOne.getName()).thenReturn("flowOne");
        Flow flowTwo = Mockito.mock(Flow.class);
        when(flowTwo.getName()).thenReturn("flowTwo");
        Flow manualFlow = Mockito.mock(Flow.class);
        when(manualFlow.getName()).thenReturn("manualFlow");
        List<Flow> flows = Arrays.asList(flowOne, flowTwo, manualFlow);
        SimpleModule simpleModule = new SimpleModule("simpleModule", flows);


        Map<String, WiretapTriggerConfiguration> wiretapsMap = new LinkedHashMap<>();
        wiretapsMap.put("flowOneAfterConsumerWiretap", new WiretapTriggerConfiguration("flowOne",
            "jmsConsumer", "AFTER", "600"));
        wiretapsMap.put("flowOneBeforeProducerWiretap", new WiretapTriggerConfiguration("flowOne",
            "jmsProducer", "BEFORE", "600"));

        StartupModuleConfiguration startupModuleConfiguration = new StartupModuleConfiguration();
        startupModuleConfiguration.setWiretaps(wiretapsMap);
        startupModuleConfiguration.setDeleteAllPreviouslySavedWiretaps(true);

        StartupModuleProcessor startupModuleProcessor = new StartupModuleProcessor(startupModuleConfiguration,
            null, jobAwareFlowEventListener);

        Map<String,String> expectedFlowOneAfterConsumerWiretapMap = new LinkedHashMap<>();
        expectedFlowOneAfterConsumerWiretapMap.put("timeToLive", "600000");
        TriggerImpl expectedFlowOneAfterConsumerWiretap = new TriggerImpl("simpleModule", "flowOne",
            "AFTER",WIRETAP_JOB, "jmsConsumer",
            expectedFlowOneAfterConsumerWiretapMap);
        Map<String,String> expectedFlowOneBeforeProducerWiretapMap = new LinkedHashMap<>();
        expectedFlowOneBeforeProducerWiretapMap.put("timeToLive", "600000");
        TriggerImpl expectedFlowOneBeforeProducerWiretap = new TriggerImpl("simpleModule", "flowOne",
            "BEFORE",WIRETAP_JOB, "jmsProducer",
            expectedFlowOneBeforeProducerWiretapMap);

        startupModuleProcessor.run(simpleModule);
        verify(jobAwareFlowEventListener, times(2)).getTriggers();
        verify(jobAwareFlowEventListener, times(1)).deleteDynamicTrigger(null);
        verify(jobAwareFlowEventListener, times(1)).addDynamicTrigger(argThat(
            new TriggerMatcher(expectedFlowOneAfterConsumerWiretap) ));
        verify(jobAwareFlowEventListener, times(1)).addDynamicTrigger(argThat(
            new TriggerMatcher(expectedFlowOneBeforeProducerWiretap) ));
        Mockito.verifyNoMoreInteractions(jobAwareFlowEventListener);
    }


    public static class TriggerMatcher implements ArgumentMatcher<Trigger> {

        private final Trigger left;

        public TriggerMatcher(Trigger left) {
            this.left = left;
        }

        // constructors

        @Override
        public boolean matches(Trigger right) {
            return
                left.getJobName().equals(right.getJobName()) &&
                left.getModuleName().equals(right.getModuleName()) &&
                left.getRelationship().equals(right.getRelationship()) &&
                left.getFlowElementName().equals(right.getFlowElementName()) &&
                left.getParams().equals(right.getParams()) &&
                left.getFlowName().equals(right.getFlowName()) ;
        }

        @Override
        public String toString() {
            return "TriggerMatcher{" +
                "left =" + left.toString() +
                '}';
        }
    }

    public static class StartupControlMatcher implements ArgumentMatcher<StartupControl> {

        private final StartupControl left;

        public StartupControlMatcher(StartupControl left) {
            this.left = left;
        }

        // constructors

        @Override
        public boolean matches(StartupControl right) {
            return left.getFlowName().equals(right.getFlowName()) &&
                left.getStartupType().equals(right.getStartupType());
        }

        @Override
        public String toString() {
            return "StartupControlMatcher{" +
                "left =" + left.toString() +
                '}';
        }
    }
}