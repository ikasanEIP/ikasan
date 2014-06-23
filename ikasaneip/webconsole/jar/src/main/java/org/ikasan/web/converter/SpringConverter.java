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
package org.ikasan.web.converter;

import org.apache.log4j.Logger;
import org.springframework.binding.convert.converters.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom converter for String into Map
 * @author Ikasan Development Team
 */
public class SpringConverter implements Converter
{
    /** Logger for this class */
    private Logger logger = Logger.getLogger(SpringConverter.class);

    /**
     * Default map entry separator when received as a single String
     */
    private String entrySeparator = ",";

    /**
     * Default map entry field separator when received as a single String.
     * Used in combo with above entrySeparator
     */
    private String fieldSeparator = "=";

    public String getEntrySeparator() {
        return entrySeparator;
    }

    public void setEntrySeparator(String entrySeparator) {
        this.entrySeparator = entrySeparator;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    @Override
    public Class getSourceClass()
    {
        return String.class;
    }

    @Override
    public Class getTargetClass()
    {
        return Map.class;
    }

    @Override
    public Object convertSourceToTargetClass(Object source, Class aClass) throws Exception
    {
        if (source == null)
        {
            return null;
        }

        if (source instanceof Map)
        {
            return source;
        }
        else if (source instanceof String)
        {
            String sourceStr = (String)source;
            Map<String, String> map = new HashMap<String, String>();

            // remove toString paranthesis
            if(sourceStr.startsWith("{"))
            {
                sourceStr = sourceStr.substring(1);
                sourceStr = sourceStr.replaceAll(", ", ",");

                if(sourceStr.endsWith("}"))
                {
                    sourceStr = sourceStr.substring(0, sourceStr.length()-1);
                }
            }
            String[] mapEntries = sourceStr.split(this.entrySeparator);
            if (mapEntries == null || mapEntries.length == 0)
            {
                return null;
            }

            for (String mapEntry : mapEntries)
            {
                String[] fields = ((String) mapEntry).split(this.fieldSeparator);
                if (fields != null)
                {
                    if (fields.length > 0)
                    {
                        if (fields.length > 1)
                        {
                            map.put(fields[0], fields[1]);
                        }
                        else
                        {
                            map.put(fields[0], null);
                        }
                    }
                }
            }

            return map;
        }
        else
        {
            logger.warn("class " + source.getClass().getName() + " has no associated converter");
        }

        return null;
    }

}

