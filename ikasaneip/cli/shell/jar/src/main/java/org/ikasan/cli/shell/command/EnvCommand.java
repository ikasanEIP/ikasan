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
package org.ikasan.cli.shell.command;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Environment command.
 *
 * @author Ikasan Developmnent Team
 */
@ShellComponent
public class EnvCommand extends AbstractCommand
{
    @Autowired
    private Environment environment;

    @ShellMethod(value = "Show runtime environment variables. Syntax: env [regexp variable name - to match specific variable names] [-names - to display variable name(s) only] [-no-expand - do not expand variable wildcards] [-list - returns results as a list]", group = "Ikasan Commands", key = "env")
    public String env(@ShellOption(defaultValue="") String variable,
                      @ShellOption(value = "-names") boolean names,
                      @ShellOption(value = "-no-expand") boolean noExpand,
                      @ShellOption(value = "-list") boolean list)
    {
        Properties props = new Properties();
        MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
            .filter(ps -> ps instanceof EnumerablePropertySource)
            .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
            .flatMap(Arrays::<String>stream)
            .forEach(propName -> props.setProperty(propName, environment.getProperty(propName)));

        Properties matchedProperties = match(props, variable);
        if(!noExpand)
        {
            matchedProperties = getExpandedPropertyValues(matchedProperties);
        }

        JSONObject jsonProps = new JSONObject(matchedProperties);
        if(names)
        {
            if(list)
            {
                StringBuilder sb = new StringBuilder();
                JSONArray jsonArray = jsonProps.names();
                for(Object name:jsonArray.toList())
                {
                     sb.append(name);
                     sb.append("\n");
                }

                return sb.toString();
            }

            return jsonProps.toString();
        }

        if(list)
        {
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,Object> entry:jsonProps.toMap().entrySet())
            {
                sb.append(entry);
                sb.append("\n");
            }
            return sb.toString();
        }

        return jsonProps.toString();
    }

    Properties getExpandedPropertyValues(Properties properties)
    {
        Properties expandedProperties = new Properties( properties.size() );
        for(Map.Entry entry : properties.entrySet())
        {
            expandedProperties.put(entry.getKey(), ProcessUtils.getCommands( (String)entry.getValue()).stream().collect(Collectors.joining(" ")));
        }

        return expandedProperties;
    }

    /**
     * Match on specifically named properties.
     * @param properties
     * @param patternToMatch
     * @return
     */
    protected Properties match(Properties properties, String patternToMatch)
    {
        Properties matchedProperties = new Properties();
        Pattern pattern = Pattern.compile(patternToMatch, Pattern.CASE_INSENSITIVE);
        for(Map.Entry entry : properties.entrySet())
        {
            Matcher matcher = pattern.matcher((String)entry.getKey());
            if(matcher.find())
            {
                matchedProperties.put(entry.getKey(), entry.getValue());
            }
        }

        return matchedProperties;
    }

}