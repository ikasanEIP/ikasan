package org.ikasan.component.endpoint.rulecheck;

import org.joda.time.format.DateTimeFormatter;

public interface DailyEventRuleStrategy<EVENT_TYPE, FAILURE_EXCEPTION extends RuleBreachException>
{
    String dateEventIsFor(EVENT_TYPE event);

    FAILURE_EXCEPTION createBreachException(String today);

    DateTimeFormatter dateFormatter();


}
