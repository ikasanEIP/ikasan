/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package org.ikasan.component.factory.spring.common;

import org.ikasan.spec.component.factory.ComponentFactory;
import org.apache.commons.lang3.StringUtils;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * For simple components this will look up both the component and factory configuration and set the configured
 * resource id correctly.
 *
 * @param <T>
 */
@Component
public abstract class BaseComponentFactory<T extends ConfiguredResource<C>, C> implements ComponentFactory<T>
{
    @Autowired
    protected Environment env;

    @Value("${module.name}") private String moduleName;

    private static Logger logger = LoggerFactory.getLogger(BaseComponentFactory.class);

    private ConfigurationHandler<C> configurationHandler;

    protected C configuration(String configPrefix, Class<C> clazz)
    {
     return configurationHandler().configuration(configPrefix, clazz);
    }

    protected C configuration(String configPrefix, String sharedConfigPrefix, Class<C> clazz)
    {
        return configurationHandler().configuration(configPrefix, sharedConfigPrefix, clazz);
    }

    protected String configuredResourceId(String nameSuffix, Class<?> clazz)
    {
        return moduleName + "-" +appendClassToNameSuffix(nameSuffix, clazz);
    }

    protected String appendClassToNameSuffix(String nameSuffix, Class<?> clazz)
    {
        if(StringUtils.isNotEmpty(nameSuffix)){
            return  nameSuffix + clazz.getSimpleName();
        }
        return  StringUtils.uncapitalize(clazz.getSimpleName());
    }


    public ConfigurationHandler<C> configurationHandler(){
        if (configurationHandler == null) {
            configurationHandler = new ConfigurationHandler<>(env);
        }
        return configurationHandler;
    }


}
