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
package org.ikasan.error.reporting.service;

import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;

/**
 * Default implementation of the ErrorReportingService.
 *
 * @author Ikasan Development Team
 */
public class ErrorReportingServiceDefaultImpl<EVENT> implements ErrorReportingService<EVENT,ErrorOccurrence>
{
    /** module name */
    String moduleName;

    /** flowName */
    String flowName;

    /** handle to the underlying DAO */
    ErrorReportingServiceDao<ErrorOccurrence> errorReportingServiceDao;

    /** allow override of timeToLive */
    Long timeToLive = ErrorReportingService.DEFAULT_TIME_TO_LIVE;

    /** need a serialiser to serialise the incoming event payload of T */
    Serialiser<EVENT,byte[]> serialiser;

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     */
    public ErrorReportingServiceDefaultImpl(String moduleName, String flowName, Serialiser<EVENT,byte[]> serialiser, ErrorReportingServiceDao<ErrorOccurrence> errorReportingServiceDao)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null'");
        }

        this.serialiser = serialiser;
        if(serialiser == null)
        {
            throw new IllegalArgumentException("serialiser cannot be 'null'");
        }

        this.errorReportingServiceDao = errorReportingServiceDao;
        if(errorReportingServiceDao == null)
        {
            throw new IllegalArgumentException("errorReportingServiceDao cannot be 'null'");
        }
    }

    @Override
    public ErrorOccurrence find(String uri)
    {
        return this.errorReportingServiceDao.find(uri);
    }

    @Override
    public String notify(String flowElementName, EVENT event, Throwable throwable)
    {
        ErrorOccurrence errorOccurrence = newErrorOccurrence(flowElementName, event, throwable);
        this.errorReportingServiceDao.save(errorOccurrence);
        return errorOccurrence.getUri();
    }

    @Override
    public String notify(String flowElementName, Throwable throwable)
    {
        ErrorOccurrence errorOccurrence = newErrorOccurrence(flowElementName, throwable);
        this.errorReportingServiceDao.save(errorOccurrence);
        return errorOccurrence.getUri();
    }

    @Override
    public void setTimeToLive(Long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    @Override
    public void housekeep()
    {
        this.errorReportingServiceDao.deleteExpired();
    }

    /**
     * Instantiate an ErrorOccurrence
     * @param flowElementName
     * @param throwable
     * @return
     */
    private ErrorOccurrence newErrorOccurrence(String flowElementName, EVENT event, Throwable throwable)
    {
        if(event instanceof FlowEvent)
        {
            FlowEvent<String,?> flowEvent = (FlowEvent)event;
            ErrorOccurrence errorOccurrence = new ErrorOccurrence(this.moduleName, this.flowName, flowElementName, this.flattenThrowable(throwable), this.timeToLive, this.serialiser.serialise(event));
            errorOccurrence.setEventLifeIdentifier(flowEvent.getIdentifier());
            errorOccurrence.setEventRelatedIdentifier(flowEvent.getRelatedIdentifier());
            return errorOccurrence;
        }

        ErrorOccurrence errorOccurrence = new ErrorOccurrence(this.moduleName, this.flowName, flowElementName, this.flattenThrowable(throwable), this.timeToLive, this.serialiser.serialise(event));
        return errorOccurrence;
    }

    /**
     * Instantiate an ErrorOccurrence
     * @param flowElementName
     * @param throwable
     * @return
     */
    private ErrorOccurrence newErrorOccurrence(String flowElementName, Throwable throwable)
    {
        return new ErrorOccurrence(this.moduleName, this.flowName, flowElementName, this.flattenThrowable(throwable), this.timeToLive);
    }

    /**
     * @param throwable
     * @return
     */
    private String flattenThrowable(Throwable throwable) {
        StringBuffer flattenedBuffer = new StringBuffer();

        Throwable cause = throwable;
        while (cause!=null){
            flattenedBuffer.append(throwable.toString());
            flattenedBuffer.append("\n");
            for (StackTraceElement stackTraceElement : cause.getStackTrace()){
                flattenedBuffer.append(stackTraceElement.toString());
                flattenedBuffer.append("\n");
            }
            if (cause.getCause()!=null){
                flattenedBuffer.append("caused by ...\n");
            }
            cause = cause.getCause();
        }
        return flattenedBuffer.toString();
    }

}