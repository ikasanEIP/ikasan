 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
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
 * @author Ikasan Development Team
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
