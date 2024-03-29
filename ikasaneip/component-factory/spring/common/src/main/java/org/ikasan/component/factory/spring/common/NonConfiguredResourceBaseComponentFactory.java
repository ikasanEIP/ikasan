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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public abstract class NonConfiguredResourceBaseComponentFactory<T,C> implements ComponentFactory<T> {
    @Autowired
    protected Environment env;

    @Value("${module.name}") private String moduleName;

    private ConfigurationHandler<C> configurationHandler;


    protected C configuration(String configPrefix, Class<C> clazz)
    {
        return configurationHandler().configuration(configPrefix, clazz);
    }

    private static Logger logger = LoggerFactory.getLogger(NonConfiguredResourceBaseComponentFactory.class);

    protected C configuration(String configPrefix, String sharedConfigPrefix, Class<C> clazz)
    {
       return configurationHandler().configuration(configPrefix, sharedConfigPrefix, clazz);
    }

    protected String configuredResourceId(String nameSuffix, Class<T> clazz)

    {
        return moduleName + "-" +appendClassToNameSuffix(nameSuffix, clazz.getSimpleName());
    }

    protected String configuredResourceId(String nameSuffix, String componentName)
    {
        return moduleName + "-" +appendClassToNameSuffix(nameSuffix, componentName);
    }

    protected String appendClassToNameSuffix(String nameSuffix, String simpleClassName)
    {
        if(StringUtils.isNotEmpty(nameSuffix)){
            return  nameSuffix + simpleClassName;
        }
        return  StringUtils.uncapitalize(simpleClassName);
    }

    public ConfigurationHandler<C> configurationHandler(){
        if (configurationHandler == null) {
            configurationHandler = new ConfigurationHandler<>(env);
        }
        return configurationHandler;
    }
}
