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
package org.ikasan.exceptionResolver;

import java.util.*;
import java.util.stream.Collectors;

import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.exceptionResolver.action.StopAction;

/**
 * Implementation of <code>IkasanExceptionHandler</code> that relies on a
 * configuration of <code>ExceptionGroup</code>s to match Throwables and apply
 * Exception Actions
 * 
 * Includes ability so set rules that are either specific to named components,
 * on non-specific
 * 
 * @author Ikasan Development Team
 * 
 */
public class MatchingExceptionResolver implements ExceptionResolver
{
    /**
     * Default Action if Throwable is not matched by any configured groups
     */
    private static final ExceptionAction defaultAction = StopAction.instance();

    /**
     * Non-component specific exception groupings
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
    public MatchingExceptionResolver(List<ExceptionGroup> exceptionGroupings, Map<String, List<ExceptionGroup>> componentExceptionGroupings)
    {
        this.exceptionGroupings = exceptionGroupings;
        this.componentExceptionGroupings = componentExceptionGroupings;
    }

    /**
     * Constructor
     * 
     * @param exceptionGroupings
     */
    public MatchingExceptionResolver(List<ExceptionGroup> exceptionGroupings)
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
    public ExceptionAction resolve(String componentName, Throwable throwable)
    {
        // try for a component match
        if (componentExceptionGroupings != null)
        {
            List<ExceptionGroup> thisComponentsGroupings = componentExceptionGroupings.get(componentName);
            Optional<ExceptionAction> result = determineExceptionAction(throwable, thisComponentsGroupings);
            if (result.isPresent())
                return result.get();
        }

        // otherwise try for a general match, otherwise return the default
        Optional<ExceptionAction> result = determineExceptionAction(throwable, exceptionGroupings);
        return result.orElse(defaultAction);
    }

    private Optional<ExceptionAction> determineExceptionAction(Throwable throwable, List<ExceptionGroup> thisComponentsGroupings)
    {
        Optional<ExceptionAction> result = Optional.empty();

        if (thisComponentsGroupings != null)
        {
            List<ExceptionGroup> acceptedExceptionGroups = thisComponentsGroupings
                .stream()
                .filter(exceptionGroup -> exceptionGroup.includes(throwable))
                .toList();

            // if one or more exception group is found, need to determine the most appropriate match.
            if (!acceptedExceptionGroups.isEmpty())
            {
                // Determine the closest match, if duplicate class definitions will take first in the incoming list.
                ExceptionAction action = findMostSpecificExceptionMatch(acceptedExceptionGroups).getAction();
                result = Optional.of(action);
            }
        }
        return result;
    }

    public static ExceptionGroup findMostSpecificExceptionMatch(List<ExceptionGroup> exceptionGroups)
    {
        return exceptionGroups.stream()
            .sorted(MatchingExceptionResolver::rankClassHierarchy)
            .findFirst()
            .get();
    }

    private static int rankClassHierarchy(ExceptionGroup group1, ExceptionGroup group2) {
        if (group1.getDefinedException().isAssignableFrom(group2.getDefinedException()))
        {
            return 1;
        }
        else if (group2.getDefinedException().isAssignableFrom(group1.getDefinedException()))
        {
            return -1;
        }
        else
        {
            return 0;
        }
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
