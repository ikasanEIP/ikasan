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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.binding.convert.converters.TwoWayConverter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom two-way converter for Map to String, uses a Jackson object mapper to write the Map as a JSON String
 *
 * @author Ikasan Development Team
 */
public class JacksonMapStringConverter implements TwoWayConverter
{
    private ObjectMapper objectMapper;

    public JacksonMapStringConverter()
    {
        this.objectMapper = new ObjectMapper();
    }

    public JacksonMapStringConverter(ObjectMapper objectMapper)
    {
        Assert.notNull(objectMapper, "ObjectMapper cannot be null");
        this.objectMapper = objectMapper;
    }

    @Override
    public Object convertSourceToTargetClass(Object source, Class<?> aClass) throws Exception
    {
        if (source == null)
        {
            return null;
        }
        return objectMapper.writeValueAsString(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convertTargetToSourceClass(Object target, Class<?> aClass)
        throws Exception
    {
        if (target == null)
        {
            return null;
        }
        Map map = objectMapper.readValue((String) target, Map.class);
        Map<String, String> checkedMap = Collections.checkedMap(new HashMap<>(map.size()), String.class, String.class);
        checkedMap.putAll(map);
        return checkedMap;
    }

    @Override
    public Class<?> getSourceClass()
    {
        return Map.class;
    }

    @Override
    public Class<?> getTargetClass()
    {
        return String.class;
    }

    public void setObjectMapper(ObjectMapper objectMapper)
    {
        Assert.notNull(objectMapper, "ObjectMapper cannot be null");
        this.objectMapper = objectMapper;
    }
}
