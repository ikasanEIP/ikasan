package org.ikasan.component.converter.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

/**
 * Validating parser callbacks
 */
public class XmlValidationEventHandler implements ValidationEventHandler
{
    /** class logger */
    private static Logger logger = LoggerFactory.getLogger(XmlValidationEventHandler.class);
    /* (non-Javadoc)
     * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
     */
    public boolean handleEvent(ValidationEvent event)
    {
        if(ValidationEvent.WARNING == event.getSeverity())
        {
            logger.warn( getLogEntry(event) );
            return true;
        }
        else if(ValidationEvent.ERROR == event.getSeverity())
        {
            throw new XmlValidationException(event);
        }
        else if(ValidationEvent.FATAL_ERROR == event.getSeverity())
        {
            throw new XmlValidationException(event);
        }
        else
        {
            throw new XmlValidationException(event);
        }
    }

    private String getLogEntry(ValidationEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getMessage());
        sb.append(event.getLinkedException());
        Object failedObject = event.getLocator().getObject();
        if(failedObject != null)
        {
            sb.append(failedObject.toString());
        }
        return sb.toString();
    }
}