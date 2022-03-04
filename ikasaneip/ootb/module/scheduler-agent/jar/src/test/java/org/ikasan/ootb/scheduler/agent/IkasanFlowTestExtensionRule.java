package org.ikasan.ootb.scheduler.agent;

import static org.quartz.TriggerBuilder.newTrigger;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class IkasanFlowTestExtensionRule extends IkasanFlowTestRule {

    public void fireScheduledConsumerWithExistingTrigger() {
        try {
            ScheduledConsumer consumer = (ScheduledConsumer) getComponent(super.getScheduledConsumerName());
            JobDetail jobDetail = ((ScheduledComponent<JobDetail>) consumer).getJobDetail();
            Trigger trigger = newTrigger()
                .withIdentity((consumer.getConfiguration().getJobName() != null && !consumer.getConfiguration().getJobName().isEmpty())
                        ? consumer.getConfiguration().getJobName() : "name",
                    (consumer.getConfiguration().getJobGroupName() != null && !consumer.getConfiguration().getJobGroupName().isEmpty())
                        ? consumer.getConfiguration().getJobGroupName() + " (manual fire)" : "group (manual fire)")
                .withDescription(consumer.getConfiguration().getDescription())
                .forJob(jobDetail).build();

            consumer.scheduleAsEagerTrigger(trigger, 0);
        } catch (SchedulerException se) {
            throw new RuntimeException(se);
        }
    }

}
