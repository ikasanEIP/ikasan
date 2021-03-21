package org.ikasan.component.factory.spring.converter;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.ikasan.component.factory.spring.IkasanComponentFactoryException;
import org.ikasan.component.validator.xml.XMLValidator;
import org.ikasan.component.validator.xml.XMLValidatorConfiguration;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.stereotype.Component;

import javax.xml.parsers.SAXParserFactory;

@Component
public class XmlValidatorFactory<S,T> extends BaseComponentFactory<XMLValidator<S,T>>
{
    // so classpath url's can be used (only gets instantiated once)
    static {
        TomcatURLStreamHandlerFactory.getInstance();
    }

    @Override public XMLValidator<S, T> create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(true);
            saxParserFactory.setNamespaceAware(true);
            XMLValidator<S,T> xmlValidator = new XMLValidator<>(saxParserFactory);
            xmlValidator.setConfiguration(configuration(configPrefix, XMLValidatorConfiguration.class));
            xmlValidator.setConfiguredResourceId(configuredResourceId(nameSuffix));
            return xmlValidator;
           } catch (Exception exc) {
            throw new IkasanComponentFactoryException(exc.getMessage(), exc);
        }
    }
}
