package org.ikasan.ootb.scheduler.agent;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledComponent;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.quartz.TriggerBuilder.newTrigger;

public class IkasanFlowTestExtensionRule extends IkasanFlowTestRule {

    private static Logger logger = LoggerFactory.getLogger(IkasanFlowTestExtensionRule.class);

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

    public void fireScheduledConsumerWithExistingTriggerEnhanced() {
        CorrelatingScheduledConsumer consumer = (CorrelatingScheduledConsumer) getComponent(super.getScheduledConsumerName());
        JobDetail jobDetail = ((ScheduledComponent<JobDetail>) consumer).getJobDetail();
        Set<Trigger> triggers = null;
        try {
            triggers = consumer.getTriggers();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        triggers.forEach(trigger -> {
            try {
                Trigger triggerToFire = newTrigger()
                    .withIdentity((consumer.getConfiguration().getJobName() != null && !consumer.getConfiguration().getJobName().isEmpty())
                            ? consumer.getConfiguration().getJobName() : "name",
                        (consumer.getConfiguration().getJobGroupName() != null && !consumer.getConfiguration().getJobGroupName().isEmpty())
                            ? consumer.getConfiguration().getJobGroupName() + " (manual fire)" : "group (manual fire)")
                    .withDescription(consumer.getConfiguration().getDescription())
                    .forJob(jobDetail).build();

                triggerToFire.getJobDataMap().putAll(trigger.getJobDataMap());
                consumer.scheduleAsEagerTrigger(triggerToFire, 0);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
