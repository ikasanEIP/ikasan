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
package org.ikasan.framework.component.transformation;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.plugins.EventInvocable;
import org.ikasan.framework.plugins.PayloadInvocable;
import org.ikasan.framework.plugins.Plugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;
import org.ikasan.spec.transformation.TransformationException;
import org.ikasan.spec.transformation.Translator;

/**
 * Adapter class for using <code>Plugin</code>'s within the Transformer framework
 * 
 * @deprecated This has been superseded by org.ikasan.framework.component.transformation.PayloadProviderTransformer 
 * @author Ikasan Development Team
 */
@Deprecated
public class PluginTransformerAdapter implements Translator
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
