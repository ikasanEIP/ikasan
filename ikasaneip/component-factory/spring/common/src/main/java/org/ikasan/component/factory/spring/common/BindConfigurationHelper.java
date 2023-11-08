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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;

import static org.ikasan.component.factory.spring.common.PropertyNameHelper.classNameToPropertyPrefix;


/**
 * Helper class used to bind configuration from spring .properties files, given a prefix
 */
public class BindConfigurationHelper
{

    private static Logger logger = LoggerFactory.getLogger(BindConfigurationHelper.class);

    public static <T> T createConfigWithPrefixAndClassName(String prefix, Class<T> clazz, Environment env){

        if (StringUtils.isEmpty(prefix)){
            logger.info("""
                            Request to create config without prefix for class [{}] wont bind to application properties, \
                             will instantiate only and use defaults\
                            """,
                    clazz.getSimpleName());
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException e) {
                throw new IkasanComponentFactoryException("Could not create configuration for class "
                        + clazz.getSimpleName(), e);
            }

        }
        String fullPrefix = StringUtils.isNotEmpty(prefix) ? prefix + "." + classNameToPropertyPrefix(clazz) :
        classNameToPropertyPrefix(clazz);
        return createConfigWithPrefix(clazz, env, fullPrefix);

    }

    public static <T> T createConfigWithPrefix(Class<T> clazz, Environment env, String fullPrefix) {
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(env);
        Binder binder = new Binder(sources, new PropertySourcesPlaceholdersResolver(env));
        return binder.bind(fullPrefix, clazz).orElseGet(() -> { throw new
                IkasanComponentFactoryException("Unable to bind properties with prefix " + fullPrefix + " to configuration "
        + " of type " + clazz.getSimpleName()
        + ". Please ensure you have defined properties for your component"); });
    }




}
