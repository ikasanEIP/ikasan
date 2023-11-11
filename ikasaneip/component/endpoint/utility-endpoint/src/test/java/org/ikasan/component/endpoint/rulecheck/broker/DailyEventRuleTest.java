package org.ikasan.component.endpoint.rulecheck.broker;


import org.ikasan.component.endpoint.rulecheck.DailyEventRule;
import org.ikasan.component.endpoint.rulecheck.DailyEventRuleConfiguration;
import org.ikasan.component.endpoint.rulecheck.DailyEventRuleStrategy;
import org.ikasan.component.endpoint.rulecheck.RuleBreachException;
import org.ikasan.component.endpoint.util.JodaFixedTimeRule;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class DailyEventRuleTest
{
    private static final DateTimeFormatter dateFormatter = forPattern("yyyyMMdd");

    public static final String INPUT = "INPUT";

    public static final RuleBreachException BREACH_EXCEPTION = new RuleBreachException("TEST");

    public static final String RULENAME = "RULENAME";

    @Rule
    public JodaFixedTimeRule jodaFixedTimeRule = new JodaFixedTimeRule();

    private DailyEventRule rule;

    private ConfigurationService configurationService;

    private ErrorReportingService errorReportingService;

    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    private DailyEventRuleConfiguration configuration;

    private DailyEventRuleStrategy dailyEventRuleStrategy;

    @BeforeEach
    void beforeEveryTest()
    {
        configuration = new DailyEventRuleConfiguration();
        configurationService = context.mock(ConfigurationService.class);
        errorReportingService = context.mock(ErrorReportingService.class);
        dailyEventRuleStrategy = context.mock(DailyEventRuleStrategy.class);
        rule = new DailyEventRule(configurationService, dailyEventRuleStrategy, RULENAME);
        rule.setErrorReportingService(errorReportingService);
        rule.setConfiguration(configuration);
        context.checking(new Expectations(){
            {
                allowing(dailyEventRuleStrategy).dateFormatter();
                will(returnValue(dateFormatter));
                allowing(dailyEventRuleStrategy).createBreachException(with(any(String.class)));
                will(returnValue(BREACH_EXCEPTION));
            }
        });
    }

    private String fixedToday()
    {
        return dateFormatter.print(jodaFixedTimeRule.getFixedTime());
    }

    @Test
    void whenEventIsForTodayShouldUpdateConfiguration(){

        context.checking(new Expectations(){
            {
                oneOf(dailyEventRuleStrategy).dateEventIsFor(INPUT);
                will(returnValue(fixedToday()));
                oneOf(configurationService).update(rule);
            }
        });

        rule.update(INPUT);
        assertThat(configuration.getLastDateProcessed(), is(equalTo(fixedToday())));
    }

    @Test
    void whenNoEventSeenTodayWeNotify() throws RuleBreachException
    {

        configuration.setLastDateProcessed("");
        context.checking(new Expectations(){
            {
                oneOf(errorReportingService).notify(RULENAME, BREACH_EXCEPTION, "Warning");
            }
        });
        rule.check(null);

    }

}