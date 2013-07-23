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
package org.ikasan.exceptionResolver.matcher;

import org.hamcrest.Matcher;
import org.ikasan.exceptionResolver.ExceptionGroup;
import org.ikasan.exceptionResolver.action.ExceptionAction;

/**
 * Default implementation of <code>ExceptionGroup</code> uses a
 * <code>TypeSafeMatcher</code> to determine if the given Throwable is a member
 * if this grouping
 * 
 * @author Ikasan Development Team
 * 
 */
public class MatcherBasedExceptionGroup implements ExceptionGroup
{
    /**
     * Underlying matcher used to determing inclusion in this grouping
     */
    private Matcher<?> matcher;

    /**
     * Bound action
     */
    private ExceptionAction action;

    /**
     * Constructor
     * 
     * @param matcher
     * @param action
     */
    public MatcherBasedExceptionGroup(Matcher<?> matcher, ExceptionAction action)
    {
        super();
        this.matcher = matcher;
        this.action = action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.exception.matching.ExceptionGroup#getAction()
     */
    public ExceptionAction getAction()
    {
        return action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.exception.matching.ExceptionGroup#includes(java.
     * lang.Throwable)
     */
    public boolean includes(Throwable throwable)
    {
        return matcher.matches(throwable);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(getClass().getName() + "[");
        sb.append("action = [" + action + "]");
        sb.append(", ");
        sb.append("matcher = [" + matcher + "]");
        sb.append("]");
        return sb.toString();
    }
}
