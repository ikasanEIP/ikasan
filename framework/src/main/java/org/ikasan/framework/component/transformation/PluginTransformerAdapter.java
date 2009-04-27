/* 
 * $Id: PluginTransformerAdapter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/PluginTransformerAdapter.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.component.transformation;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.plugins.EventInvocable;
import org.ikasan.framework.plugins.PayloadInvocable;
import org.ikasan.framework.plugins.Plugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Adapter class for using <code>Plugin</code>'s within the Transformer framework
 * 
 * @deprecated This has been superseded by org.ikasan.framework.component.transformation.PayloadProviderTransformer 
 * @author Ikasan Development Team
 */
@Deprecated
public class PluginTransformerAdapter implements Transformer
{
    /** Plugin being wrapped */
    private Plugin plugin;

    /**
     * Constructor
     * 
     * @param plugin The plugin to use
     */
    public PluginTransformerAdapter(Plugin plugin)
    {
        super();
        this.plugin = plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws TransformationException
    {
        try
        {
            if (plugin instanceof EventInvocable)
            {
                ((EventInvocable) plugin).invoke(event);
            }
            else
            {
                for (Payload payload : event.getPayloads())
                {
                    ((PayloadInvocable) plugin).invoke(payload);
                }
            }
        }
        catch (PluginInvocationException e)
        {
            throw new TransformationException(e);
        }
    }
}
