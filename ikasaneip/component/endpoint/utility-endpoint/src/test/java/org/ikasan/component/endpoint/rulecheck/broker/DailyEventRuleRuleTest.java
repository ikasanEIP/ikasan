package org.ikasan.component.endpoint.rulecheck.broker;


import org.ikasan.component.endpoint.rulecheck.DailyEventRuleConfiguration;
import org.ikasan.component.endpoint.rulecheck.DailyEventRuleRule;
import org.ikasan.component.endpoint.rulecheck.DailyEventRuleStrategy;
import org.ikasan.component.endpoint.rulecheck.RuleBreachException;
import org.ikasan.component.endpoint.util.JodaFixedTimeRule;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertThat;

public class DailyEventRuleRuleTest
{
    private static final DateTimeFormatter dateFormatter = forPattern("yyyyMMdd");

    public static final String INPUT = "INPUT";

    public static final RuleBreachException BREACH_EXCEPTION = new RuleBreachException("TEST");

    public static final String RULENAME = "RULENAME";

    @Rule
    public JodaFixedTimeRule jodaFixedTimeRule = new JodaFixedTimeRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DailyEventRuleRule rule;

    private ConfigurationService configurationService;

    private ErrorReportingService errorReportingService;

    private Mockery context = new Mockery()
    {
        {
            //setThreadingPolicy(new Synchroniser());
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private DailyEventRuleConfiguration configuration;

    private DailyEventRuleStrategy dailyEventRuleStrategy;

    @Before
    public void beforeEveryTest()
    {
        configuration = new DailyEventRuleConfiguration();
        configurationService = context.mock(ConfigurationService.class);
        errorReportingService = context.mock(ErrorReportingService.class);
        dailyEventRuleStrategy = context.mock(DailyEventRuleStrategy.class);
        rule = new DailyEventRuleRule(configurationService, dailyEventRuleStrategy, RULENAME);
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
    public void whenEventIsForTodayShouldUpdateConfiguration(){

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
    public void whenNoEventSeenTodayWeNotify() throws RuleBreachException
    {

        configuration.setLastDateProcessed("");
        context.checking(new Expectations(){
            {
                oneOf(errorReportingService).notify(RULENAME, BREACH_EXCEPTION);
            }
        });
        rule.check(null);

    }

}