/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.context;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.ikasan.common.CommonContext;
import org.ikasan.common.CommonRuntimeException;

/**
 * This class fronts the context access and provides simple methods for
 * lookups, binds, unbinds and rebinds for objects within the current context.
 * 
 * @author Ikasan Development Team
 */
public class DefaultContext
    implements CommonContext
{
    /** context instance */
    private Context context;

    /**
     * Default constructor
     * Creates a new default context
     * @throws CommonRuntimeException 
     */
    public DefaultContext()
    {
        try
        {
            this.context = new InitialContext();
        }
        catch(NamingException e)
        {
            throw new CommonRuntimeException(e);
        }
    }

    /**
     * Properties based constructor creates a new context based on the incoming
     * properties.
     * @param properties
     * @throws CommonRuntimeException 
     */
    public DefaultContext(final Properties properties)
    {
        try
        {
            this.context = new InitialContext(properties);
        }
        catch(NamingException e)
        {
            throw new CommonRuntimeException(e);
        }
    }

    /**
     * Lookup based on the provided context for the given object name.
     * @param object context
     * @param objectName
     * @return Object
     * @throws NamingException
     */
    public Object lookup(Object object, String objectName)
        throws NamingException
    {
        if(object instanceof Context)
            return ((Context)object).lookup(objectName);
        else if(object instanceof Properties)
        {
            Context ctx = new InitialContext((Properties)object);
            return ctx.lookup(objectName);
        }
        else
            throw new NamingException("Object was not a valid Context for "
                    + "lookup of [" + objectName + "].");
    }

    /**
     * Lookup on the current context for the given object name.
     * @param objectName
     * @return Object
     * @throws NamingException
     */
    public Object lookup(String objectName)
        throws NamingException
    {
        return this.context.lookup(objectName);
    }

    /**
     * Bind the objectValue to the objectName in the given Context.
     * @param object 
     * @param objectName 
     * @param objectValue 
     * @throws NamingException 
     */
    public void bind(Object object, String objectName, Object objectValue)
        throws NamingException
    {
        if(object instanceof Context)
        {
            Context ctx = (Context)object;
            Name name = ctx.getNameParser("").parse(objectName);
            bind(ctx, name, objectValue);
        }
        else if(object instanceof Properties)
        {
            Context ctx = new InitialContext((Properties)object);
            Name name = ctx.getNameParser("").parse(objectName);
            bind(ctx, name, objectValue);
        }
        else
            throw new NamingException("Object was not a valid Context for "
                    + "lookup of [" + objectName + "].");
    }

    /**
     * Bind the objectValue to the objectName in the current Context.
     * @param objectName 
     * @param objectValue 
     * @throws NamingException 
     */
    public void bind(String objectName, Object objectValue)
        throws NamingException
    {
        Name name = this.context.getNameParser("").parse(objectName); //$NON-NLS-1$
        bind(this.context, name, objectValue);
    }

    /**
     * Utility method for binding objectValues to object Names in the context.
     * @param ctx
     * @param name
     * @param value
     * @throws NamingException
     */
    private void bind(Context ctx, Name name, Object value)
        throws NamingException
    {
        int size = name.size();
        String atom = name.get(size - 1);
        Context parentCtx = createSubcontext(ctx, name.getPrefix(size - 1));
        parentCtx.bind(atom, value);
    }
    
    /**
     * Unbind an object from the given context
     * 
     * @param object
     * @param objectName
     * @throws NamingException
     */
    public void unbind(Object object, String objectName)
        throws NamingException
    {
        if(object instanceof Context)
        {
            Context ctx = (Context)object;
            ctx.unbind(ctx.getNameParser("").parse(objectName));
        }
        else if(object instanceof Properties)
        {
            Context ctx = new InitialContext((Properties)object);
            ctx.unbind(ctx.getNameParser("").parse(objectName));
        }
        else
        {
            throw new NamingException("Object was not a valid Context for "
                    + "lookup of [" + objectName + "].");
        }
    }

    /**
     * Unbind an object from the current context
     * 
     * @param objectName
     * 
     * @throws NamingException
     */
    public void unbind(String objectName)
        throws NamingException
    {
        this.context.unbind(this.context.getNameParser("").parse(objectName)); //$NON-NLS-1$
    }

    /**
     * Rebind an existing object to the given name in the given context
     * 
     * @param object
     * @param objectName
     * @param objectValue 
     * @throws NamingException
     */
    public void rebind(Object object, String objectName, Object objectValue)
        throws NamingException
    {
        if(object instanceof Context)
        {
            Context ctx = (Context)object;
            rebind(ctx, ctx.getNameParser("").parse(objectName), objectValue);
        }
        else if(object instanceof Properties)
        {
            Context ctx = new InitialContext((Properties)object);
            rebind(ctx, ctx.getNameParser("").parse(objectName), objectValue);
        }
        else
            throw new NamingException("Object was not a valid Context for "
                    + "lookup of [" + objectName + "].");
    }

    /**
     * Rebind an existing object to the given name in the current context.
     * 
     * @param objectName
     * @param objectValue 
     * @throws NamingException
     */
    public void rebind(String objectName, Object objectValue)
        throws NamingException
    {
        rebind(this.context, objectName, objectValue);
    }

    /**
     * Rebind wrapper allowing objectName for binding to be a string.
     * 
     * @param ctx
     * @param objectName
     * @param objectValue
     * @throws NamingException
     */
    private void rebind(Context ctx, String objectName, Object objectValue)
        throws NamingException
    {
        rebind(ctx, ctx.getNameParser("").parse(objectName), objectValue); //$NON-NLS-1$
    } 

    /**
     * Actual rebinding method
     *  
     * @param ctx
     * @param name
     * @param value
     * @throws NamingException
     */
    private void rebind(Context ctx, Name name, Object value)
        throws NamingException
    {
        int size = name.size();
        String atom = name.get(size - 1);
        Context parentCtx = createSubcontext(ctx, name.getPrefix(size - 1));
        parentCtx.rebind(atom, value);
    }

    /**
     * Utility method for iterating over a given context and creating
     * all required sub-contexts.
     * 
     * @param ctx
     * @param name
     * @return Context
     * @throws NamingException
     */
    private Context createSubcontext(Context ctx, Name name)
        throws NamingException
    {
        Context tempCtx = ctx;
        Context subctx = ctx;
        for (int pos = 0; pos < name.size(); pos++)
        {
            String ctxName = name.get(pos);
            try
            {
                subctx = (Context) tempCtx.lookup(ctxName);
            } catch (NameNotFoundException e)
            {
                subctx = tempCtx.createSubcontext(ctxName);
            }
            
            // The current subctx will be the
            // ctx for the next name component
            tempCtx = subctx;
        }
        return subctx;
    }

}
