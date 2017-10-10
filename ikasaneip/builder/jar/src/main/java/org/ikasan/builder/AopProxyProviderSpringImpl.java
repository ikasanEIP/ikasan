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
package org.ikasan.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AOP Proxy Provider implementation based around Spring.
 *
 * @author Ikasan Development Team
 */
public class AopProxyProviderSpringImpl implements AopProxyProvider, ApplicationContextAware
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(AopProxyProviderSpringImpl.class);

    /** map of defined pointcuts */
    Map<String,DefaultBeanFactoryPointcutAdvisor> aopFactories;

    /**
     * Apply pointcuts for the given name and component.
     * @param name
     * @param component
     * @param <T>
     * @return
     */
    public <T> T applyPointcut(String name, T component)
    {
        List<? extends Class<?>> componentInterfaces = Arrays.asList(component.getClass().getInterfaces());

        if(this.aopFactories!=null && !this.aopFactories.isEmpty())
        {
            ProxyFactory factory = new ProxyFactory(component);

            for (String key : this.aopFactories.keySet())
            {
                DefaultBeanFactoryPointcutAdvisor aopFactory = this.aopFactories.get(key);
                for(Class componentInterface :componentInterfaces)
                {
                    if (aopFactory.getPointcut().getClassFilter().matches(componentInterface))
                    {
                        logger.info("Applying pointcut [" + key + "] on component [" + name + "] class [" + component.getClass().getCanonicalName() + "] interface [" + componentInterface.getCanonicalName() + "]");
                        factory.addInterface(componentInterface);
                        factory.addAdvice(aopFactory.getAdvice());
                    }
                }
            }
            if(factory.getAdvisors().length>0)
            {
                return (T) factory.getProxy();
            }else
            {
                return component;
            }
        }
        return component;
    }

    // FIXME - find a better way to get hold of the aopFactories
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.aopFactories = applicationContext.getBeansOfType(DefaultBeanFactoryPointcutAdvisor.class);
    }
}