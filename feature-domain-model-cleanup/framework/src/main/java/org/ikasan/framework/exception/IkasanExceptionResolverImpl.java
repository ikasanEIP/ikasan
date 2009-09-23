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
package org.ikasan.framework.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonException;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.ExceptionType;

/**
 * Concrete implementation of the Ikasan Exception Resolver. This class resolves the thrown exception to an associated
 * IkasanExceptionDefinition which is returned to the caller for handling.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionResolverImpl implements IkasanExceptionResolver
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(IkasanExceptionResolverImpl.class);

    /** Line separator */
    private static final String EOL = System.getProperty("line.separator");

    /** Generic module exception definitions */
    private List<DefaultExceptionDefinition> moduleExceptionDefinitions;

    /** Component specific exception definitions */
    private Map<String, List<DefaultExceptionDefinition>> componentExceptionDefinitions;

    /**
     * Constructor
     * 
     * @param moduleExceptionDefinitions - The module exception definitions
     * @param componentExceptionDefinitions map
     */
    public IkasanExceptionResolverImpl(List<DefaultExceptionDefinition> moduleExceptionDefinitions,
            Map<String, List<DefaultExceptionDefinition>> componentExceptionDefinitions)
    {
        this.moduleExceptionDefinitions = moduleExceptionDefinitions;
        this.componentExceptionDefinitions = componentExceptionDefinitions;
    }

    /**
     * Constructor
     * 
     * @param moduleExceptionDefinitions - The module exception definitions
     */
    public IkasanExceptionResolverImpl(List<DefaultExceptionDefinition> moduleExceptionDefinitions)
    {
        this(moduleExceptionDefinitions, null);
    }
    
    /**
     * Resolve the given exception into a specific <code>IkasanExceptionDefinition</code>
     * 
     * @param thrown Exception that was thrown
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    public IkasanExceptionResolution resolve(Throwable thrown) throws IkasanExceptionResolutionNotFoundException
    {
        return this.resolveForModule(thrown);
    }

    /**
     * Resolve the given component name and exception into a specific <code>IkasanExceptionDefinition</code>
     * 
     * @param componentName The name of the component that threw the Exception
     * @param thrown Exception that was thrown
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    public IkasanExceptionResolution resolve(String componentName, Throwable thrown)
            throws IkasanExceptionResolutionNotFoundException
    {
        // check to see if we have component specific exception defs
        if (this.componentExceptionDefinitions != null)
        {
            return this.resolveForComponent(componentName, thrown);
        }
        logger.warn("ComponentName [" + componentName + "] has no defined exceptions. "
                + "Resorting to matching against module defined exceptions.");
        return this.resolveForModule(thrown);
    }

    /**
     * Module exception resolution takes two passes at resolving the thrown. Pass 1 tries to match the exception and
     * exception type against the module exception definitions; Pass 2 tries to match only the exception against the
     * module exception definitions;
     * 
     * @param thrown Exception that was thrown
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    private IkasanExceptionResolution resolveForModule(Throwable thrown)
            throws IkasanExceptionResolutionNotFoundException
    {
        // use a string builder to track all failed matches for potential
        // exception text if no matches are found.
        StringBuilder sb = new StringBuilder();
        // try module E:T match
        try
        {
            return this.matchExceptionAndType(this.moduleExceptionDefinitions, thrown, this.getExceptionType(thrown));
        }
        catch (IkasanExceptionResolutionNotFoundException e)
        {
            sb.append(e.getMessage());
            logger.debug(sb, e);
            // try module E match
            try
            {
                return this.matchExceptionOnly(this.moduleExceptionDefinitions, thrown);
            }
            catch (IkasanExceptionResolutionNotFoundException e1)
            {
                // we've just left the last chance saloon without any matches
                // so we should dump the thrown stackTrace
                // to give support staff enough info to deal with this issue
                sb.append(e1.getMessage());
                sb.append(EOL + thrown.getClass().getName() + ": " + thrown.getMessage() + EOL);
                for (StackTraceElement stackTraceElement : thrown.getStackTrace())
                {
                    sb.append("  at " + stackTraceElement + EOL);
                }
                throw new IkasanExceptionResolutionNotFoundException(sb.toString());
            }
        }
    }

    /**
     * Component exception resolution takes four passes at resolving the thrown. Pass 1 tries to match the exception and
     * exception type against the component exception definitions; Pass 2 tries to match the exception and exception
     * type against the module exception definitions; Pass 3 tries only the exception against the component exception
     * definitions; Pass 4 tries only the exception against the module exception definitions;
     * 
     * @param componentName The name of the component that threw the Exception
     * @param thrown Exception that was thrown
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    private IkasanExceptionResolution resolveForComponent(String componentName, Throwable thrown)
            throws IkasanExceptionResolutionNotFoundException
    {
        // use a string builder to track all failed matches for potential
        // exception text if no matches are found.
        StringBuilder sb = new StringBuilder();
        // try component E:T match
        try
        {
            return this.matchExceptionAndType(this.componentExceptionDefinitions.get(componentName), thrown, this
                .getExceptionType(thrown));
        }
        catch (IkasanExceptionResolutionNotFoundException e)
        {
            sb.append(e.getMessage());
            logger.debug(sb, e);
            // try module E:T match
            try
            {
                return this.matchExceptionAndType(this.moduleExceptionDefinitions, thrown, this
                    .getExceptionType(thrown));
            }
            catch (IkasanExceptionResolutionNotFoundException e1)
            {
                sb.append(e1.getMessage());
                logger.debug(sb, e1);
                // try component E match
                try
                {
                    return this.matchExceptionOnly(this.componentExceptionDefinitions.get(componentName), thrown);
                }
                catch (IkasanExceptionResolutionNotFoundException e2)
                {
                    sb.append(e2.getMessage());
                    logger.debug(sb, e2);
                    try
                    {
                        // try module E match
                        return this.matchExceptionOnly(this.moduleExceptionDefinitions, thrown);
                    }
                    catch (IkasanExceptionResolutionNotFoundException e3)
                    {
                        sb.append(e3.getMessage());
                        logger.debug(sb, e3);
                        throw new IkasanExceptionResolutionNotFoundException(sb.toString());
                    }
                }
            }
        }
    }

    /**
     * Get the exceptionType from the incoming thrown.
     * 
     * @param thrown The Exception that was thrown
     * @return ExceptionType or 'null'
     */
    private ExceptionType getExceptionType(final Throwable thrown)
    {
        if (thrown instanceof CommonException)
        {
            return ((CommonException) thrown).getExceptionType();
        }
        else if (thrown instanceof CommonRuntimeException)
        {
            return ((CommonRuntimeException) thrown).getExceptionType();
        }
        return null;
    }

    /**
     * Match an exception by going through all exceptionDefs for each wrapped exception in the thrown instance starting
     * from the originating exception.
     * 
     * @param exceptionDefs List of exception definitions
     * @param thrown The Exception that was thrown
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    private IkasanExceptionResolution matchExceptionOnly(List<DefaultExceptionDefinition> exceptionDefs,
            Throwable thrown) throws IkasanExceptionResolutionNotFoundException
    {
        if (exceptionDefs == null || thrown == null)
        {
            throw new IkasanExceptionResolutionNotFoundException(
                "Unable to match thrown exception or exceptionDefs of 'null'. ");
        }
        // Use a string builder to track all failed matches for potential
        // exception text if no matches are found.
        StringBuilder sb = new StringBuilder();
        // Iterate through the causes of this exception starting at original
        for (Throwable t : getCauses(thrown))
        {
            String className = t.getClass().getName();
            for (DefaultExceptionDefinition exceptionDef : exceptionDefs)
            {
                if (className.equals(exceptionDef.getClassName()))
                {
                    return exceptionDef.getResolution();
                }
            }
            sb.append("Exception classname [" + className + "] not matched. ");
        }
        // no matches at all
        throw new IkasanExceptionResolutionNotFoundException(sb.toString());
    }

    /**
     * Match an exception and type by going through all exceptionDefs for each wrapped exception in the thrown instance
     * starting from the originating exception.
     * 
     * @param exceptionDefs List of exception definitions
     * @param thrown The Exception that was thrown
     * @param exceptionType The exception type to match on
     * @return IkasanExceptionResolution
     * @throws IkasanExceptionResolutionNotFoundException Exception if resolution is not found
     */
    private IkasanExceptionResolution matchExceptionAndType(List<DefaultExceptionDefinition> exceptionDefs,
            Throwable thrown, ExceptionType exceptionType) throws IkasanExceptionResolutionNotFoundException
    {
        if (exceptionDefs == null || thrown == null || exceptionType == null)
        {
            throw new IkasanExceptionResolutionNotFoundException(
                "Unable to match thrown exception, exceptionDefs, or exceptionType of 'null'. ");
        }
        // Use a string builder to track all failed matches for potential
        // exception text if no matches are found.
        StringBuilder sb = new StringBuilder();
        // Iterate through the causes of this exception starting at original
        for (Throwable t : getCauses(thrown))
        {
            String className = t.getClass().getName();
            for (DefaultExceptionDefinition exceptionDef : exceptionDefs)
            {
                if (className.equals(exceptionDef.getClassName()) && exceptionType.equals(exceptionDef.getType()))
                    return exceptionDef.getResolution();
            }
            sb.append("Exception classname [" + className + "] exceptionType [" + exceptionType.getName()
                    + "] not matched. ");
        }
        // no matches at all
        throw new IkasanExceptionResolutionNotFoundException(sb.toString());
    }

    /**
     * Get all the possible wrapped causes in the thrown exception and return these in order of root cause working
     * backwards.
     * 
     * @param thrown The exception that was thrown
     * @return List<Throwable> causes
     */
    private static List<Throwable> getCauses(final Throwable thrown)
    {
        List<Throwable> throwableList = new ArrayList<Throwable>();
        //
        // iterate through the stack of causes and put each into the list
        Throwable cause = thrown;
        do
        {
            throwableList.add(cause);
            cause = cause.getCause();
        }
        while (cause != null);
        // reverse the list to make life easier for the caller
        // first copy the list
        List<Throwable> reversedList = new ArrayList<Throwable>(throwableList);
        // now reverse the order
        int throwableListCount = throwableList.size();
        for (Throwable t : throwableList)
        {
            reversedList.set(throwableListCount - 1, t);
            throwableListCount--;
        }
        return reversedList;
    }
}
