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
package org.ikasan.exceptionHandler;

import java.util.List;
import java.util.Map;

import org.ikasan.exceptionHandler.action.ExceptionAction;
import org.ikasan.exceptionHandler.action.StopAction;

/**
 * Implementation of <code>IkasanExceptionHandler</code> that relies on a
 * configuration of <code>ExceptionGroup</code>s to match Throwables and apply
 * Exception Actions
 * 
 * Includes ability so set rules that are either specific to named components,
 * on non specific
 * 
 * @author Ikasan Development Team
 * 
 */
public class MatchingExceptionHandler implements ExceptionHandler
{
    /**
     * Default Action if Throwable is not matched by any configured groups
     */
    private static final ExceptionAction defaultAction = StopAction.instance();

    /**
     * Non component specific exception groupings
     */
    private List<ExceptionGroup> exceptionGroupings;

    /**
     * Component specific exception groupings keyed by component name
     */
    private Map<String, List<ExceptionGroup>> componentExceptionGroupings;

    /**
     * Constructor
     * 
     * @param exceptionGroupings
     * @param componentExceptionGroupings
     */
    public MatchingExceptionHandler(List<ExceptionGroup> exceptionGroupings, Map<String, List<ExceptionGroup>> componentExceptionGroupings)
    {
        this.exceptionGroupings = exceptionGroupings;
        this.componentExceptionGroupings = componentExceptionGroupings;
    }

    /**
     * Constructor
     * 
     * @param exceptionGroupings
     */
    public MatchingExceptionHandler(List<ExceptionGroup> exceptionGroupings)
    {
        this(exceptionGroupings, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.component.IkasanExceptionHandler#handleThrowable
     * (java.lang.String, java.lang.Throwable)
     */
    public ExceptionAction handleThrowable(String componentName, Throwable throwable)
    {
        // try for a component match
        if (componentExceptionGroupings != null)
        {
            List<ExceptionGroup> thisComponentsGroupings = componentExceptionGroupings.get(componentName);
            if (thisComponentsGroupings != null)
            {
                for (ExceptionGroup exceptionGroup : thisComponentsGroupings)
                {
                    if (exceptionGroup.includes(throwable))
                    {
                        return exceptionGroup.getAction();
                    }
                }
            }
        }
        // otherwise try for a general match
        if (exceptionGroupings != null)
        {
            for (ExceptionGroup exceptionGroup : exceptionGroupings)
            {
                if (exceptionGroup.includes(throwable))
                {
                    return exceptionGroup.getAction();
                }
            }
        }
        // otherwise return the default
        return defaultAction;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(getClass().getName() + " [");
        sb.append("exceptionGroupings = [" + exceptionGroupings + "]");
        sb.append(", ");
        sb.append("componentExceptionGroupings = [" + componentExceptionGroupings + "]");
        sb.append(", ");
        sb.append("defaultAction = [" + defaultAction + "]");
        sb.append("]");
        return sb.toString();
    }
}
