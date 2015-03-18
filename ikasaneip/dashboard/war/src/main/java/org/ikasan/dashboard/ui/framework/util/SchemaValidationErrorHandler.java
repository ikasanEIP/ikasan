/*
 * $Id: SchemaValidationErrorHandler.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/SchemaValidationErrorHandler.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.mappingconfiguration.window.MappingConfigurationValuesImportWindow;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author CMI2 Development Team
 *
 */
public class SchemaValidationErrorHandler implements ErrorHandler
{
    private static Logger logger = Logger.getLogger(MappingConfigurationValuesImportWindow.class);

    private boolean inError = false;

    private List<SAXParseException> warnings = new ArrayList<SAXParseException>();
    private List<SAXParseException> errors = new ArrayList<SAXParseException>();
    private List<SAXParseException> fatal = new ArrayList<SAXParseException>();
    
    public void warning(SAXParseException e) throws SAXException {
        logger.info(e.getMessage());
        warnings.add(e);
    }

    public void error(SAXParseException e) throws SAXException {
        logger.info(e.getMessage());
        errors.add(e);
        inError = true;
    }

    public void fatalError(SAXParseException e) throws SAXException {
        logger.info(e.getMessage());
        fatal.add(e);
        inError = true;
    }

    /**
     * @return the warnings
     */
    public List<SAXParseException> getWarnings()
    {
        return warnings;
    }

    /**
     * @param warnings the warnings to set
     */
    public void setWarnings(List<SAXParseException> warnings)
    {
        this.warnings = warnings;
    }

    /**
     * @return the errors
     */
    public List<SAXParseException> getErrors()
    {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List<SAXParseException> errors)
    {
        this.errors = errors;
    }

    /**
     * @return the fatal
     */
    public List<SAXParseException> getFatal()
    {
        return fatal;
    }

    /**
     * @param fatal the fatal to set
     */
    public void setFatal(List<SAXParseException> fatal)
    {
        this.fatal = fatal;
    }

    /**
     * @return the inError
     */
    public boolean isInError()
    {
        return inError;
    }
}
