package org.ikasan.dashboard.ui.framework.validator;

import com.vaadin.data.validator.AbstractValidator;
import org.quartz.CronExpression;

/**
 * Created by stewmi on 25/08/2016.
 */
public class QuartzCronExpressionValidator extends AbstractValidator<String>
{

    /**
     * @param errorMessage
     */
    public QuartzCronExpressionValidator(String errorMessage)
    {
        super(errorMessage);
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.validator.AbstractValidator#isValidValue(java.lang.Object)
     */
    @Override
    protected boolean isValidValue(String value)
    {
        try
        {
            CronExpression.validateExpression(value);
        }
        catch (Exception e)
        {
            super.setErrorMessage("Invalid cron expression: " + e.getMessage());
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.validator.AbstractValidator#getType()
     */
    @Override
    public Class<String> getType()
    {
        return String.class;
    }

}
