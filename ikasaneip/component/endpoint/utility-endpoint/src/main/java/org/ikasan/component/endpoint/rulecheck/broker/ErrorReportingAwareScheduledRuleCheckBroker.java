package org.ikasan.component.endpoint.rulecheck.broker;

import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.broker.ScheduledRuleCheckBroker;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.quartz.Scheduler;

public class ErrorReportingAwareScheduledRuleCheckBroker<EVENT, RULE extends IsErrorReportingServiceAware> extends
        ScheduledRuleCheckBroker<EVENT> implements
        IsErrorReportingServiceAware
{
    private final Rule rule;

    public ErrorReportingAwareScheduledRuleCheckBroker(Scheduler scheduler,
            Rule rule)
    {
        super(scheduler, rule);
        this.rule = rule;
    }

    @Override
    public void setErrorReportingService(ErrorReportingService errorReportingService)
    {
        if(rule instanceof IsErrorReportingServiceAware)
            ((IsErrorReportingServiceAware) rule).setErrorReportingService(errorReportingService);
    }
}
