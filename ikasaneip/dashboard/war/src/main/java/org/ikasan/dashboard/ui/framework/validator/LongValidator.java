/*
 * $Id: LongValidator.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/validator/LongValidator.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.validator;

import org.apache.log4j.Logger;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.ComboBox;

/**
 * @author CMI2 Development Team
 *
 */
public class LongValidator  extends AbstractValidator<Long>
{
    private static final long serialVersionUID = 5233659561482627992L;

    /**
     * @param errorMessage
     */
    public LongValidator(String errorMessage)
    {
        super(errorMessage);
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.validator.AbstractValidator#isValidValue(java.lang.Object)
     */
    @Override
    protected boolean isValidValue(Long value)
    {
        if(value == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.validator.AbstractValidator#getType()
     */
    @Override
    public Class<Long> getType()
    {
        return Long.class;
    }

}
