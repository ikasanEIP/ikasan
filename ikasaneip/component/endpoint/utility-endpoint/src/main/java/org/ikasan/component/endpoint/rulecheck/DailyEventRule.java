package org.ikasan.component.endpoint.rulecheck;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;

import static org.joda.time.DateTime.now;

public class DailyEventRule<PAYLOAD_TYPE, BREACH_EXCEPTION_TYPE extends RuleBreachException>
        implements Rule<PAYLOAD_TYPE, Object>, ConfiguredResource<DailyEventRuleConfiguration>,
        IsErrorReportingServiceAware
{
    private static Logger logger = LoggerFactory.getLogger(DailyEventRule.class);

    private DailyEventRuleConfiguration configuration;

    public ConfigurationService<DailyEventRule> configurationService;

    private final String ruleName;

    private final DailyEventRuleStrategy<PAYLOAD_TYPE, BREACH_EXCEPTION_TYPE> dailyEventRuleStrategy;

    private String id;

    private ErrorReportingService errorReportingService;

    public DailyEventRule(ConfigurationService<DailyEventRule> configurationService,
                          DailyEventRuleStrategy<PAYLOAD_TYPE, BREACH_EXCEPTION_TYPE> dailyEventRuleStrategy, String ruleName)
    {
        this.configurationService = configurationService;
        this.dailyEventRuleStrategy = dailyEventRuleStrategy;
        this.ruleName = ruleName;
    }

    @Override
    public void rebase()
    {
    }

    @Override
    public void update(PAYLOAD_TYPE payload)
    {
        String date = dailyEventRuleStrategy.dateEventIsFor(payload);
        logger.debug("Date event for [" + date + "]");
        recordProcessedIfForToday(date);
    }

    private void recordProcessedIfForToday(String date)
    {
        if (today().equals(date))
        {
            logger.debug("Storing record of seeing valid event for [" + date + "]");
            configuration.setLastDateProcessed(date);
            configurationService.update(this);
        }
    }

    @Override
    public void check(Object o) throws RuleBreachException
    {
        String today = today();
        logger.debug(
                "Checking if we have seen a valid event on [ " + today + "] comparing it to recorded of [" + configuration
                        .getLastDateProcessed() + "]");
        if (!today.equals(configuration.getLastDateProcessed()))
            errorReportingService.notify(ruleName, dailyEventRuleStrategy.createBreachException(today), "Warning");
    }

    private String today()
    {
        return dailyEventRuleStrategy.dateFormatter().print(now());
    }

    @Override
    public String getConfiguredResourceId()
    {
        return id;
    }

    @Override
    public void setConfiguredResourceId(String id)
    {
        this.id = id;
        if (dailyEventRuleStrategy instanceof ConfiguredResource resource)
            resource.setConfiguredResourceId(id);
    }

    @Override
    public void setErrorReportingService(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
    }

    @Override
    public DailyEventRuleConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(DailyEventRuleConfiguration configuration)
    {
        this.configuration = configuration;
        if (dailyEventRuleStrategy instanceof ConfiguredResource resource)
            resource.setConfiguration(configuration);
    }
}
