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
package org.ikasan.component.converter.xml;

import jakarta.xml.bind.JAXBElement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfigurationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * Uses Jaxb2Marshaller to unmarshall xml to java object
 * 
 * @author edwaki
 */
public class XmlStringToObjectConverter<SOURCE,TARGET> implements Converter<SOURCE, TARGET>, ConfiguredResource<XmlStringToObjectConfiguration>
{

    private XmlStringToObjectConfiguration configuration;
    private String configuredResourceId;
    private Jaxb2Marshaller marshaller;
    
    @Override
    public XmlStringToObjectConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(XmlStringToObjectConfiguration configuration)
    {
        this.configuration = configuration;
        marshaller = new Jaxb2Marshaller();
        if (StringUtils.isNotEmpty(configuration.getContextPath())){
            marshaller.setContextPath(configuration.getContextPath());
        } else if (ArrayUtils.isNotEmpty(configuration.getContextPaths())){
            marshaller.setContextPaths(configuration.getContextPaths());
        } else if (ArrayUtils.isNotEmpty(configuration.getClassesToBeBound())){
            marshaller.setClassesToBeBound(configuration.getClassesToBeBound());
        }
        marshaller.setUnmarshallerProperties(configuration.getUnmarshallerProperties());
        marshaller.setMarshallerProperties(configuration.getMarshallerProperties());
        if (configuration.getValidationEventHandler() != null)
        {
            marshaller.setValidationEventHandler(configuration.getValidationEventHandler());
        }

        try
        {
            marshaller.afterPropertiesSet();
        }
        catch (Exception e)
        {
            throw new ConfigurationException(e.getMessage());
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId=configuredResourceId;
    }

    @Override
    public TARGET convert(SOURCE payload) throws TransformationException
    {
        Object result = null;

        try
        {
            String xml = null;
            if (payload instanceof String string){
                xml = string;
            } else if (payload instanceof byte[] bytes){
                xml = new String(bytes);
            } else {
                throw new TransformationException("Cannot get xml string from payload " + payload.toString());
            }
            result = marshaller.unmarshal(new StreamSource(new StringReader(xml)));
            if(result instanceof JAXBElement element && configuration.isAutoConvertElementToValue())
            {
                result = element.getDeclaredType().cast(element.getValue());
                return (TARGET)result;
            }

            return (TARGET)result;
        }
        catch (XmlMappingException e)
        {
            throw new TransformationException(e);
        }
        catch (ClassCastException e)
        {
            throw new TransformationException("Expected TARGET type on your Converter doesn't match actual return type.", e);
        }
    }

}
