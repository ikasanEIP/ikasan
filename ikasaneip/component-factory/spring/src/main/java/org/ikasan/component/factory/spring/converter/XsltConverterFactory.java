package org.ikasan.component.factory.spring.converter;

import liquibase.pro.packaged.S;
import liquibase.pro.packaged.T;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.ikasan.component.converter.xml.XsltConverter;
import org.ikasan.component.converter.xml.XsltConverterConfiguration;
import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.springframework.stereotype.Component;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

@Component
public class XsltConverterFactory<S,T> extends BaseComponentFactory<XsltConverter<S, T>>
{

    // so classpath url's can be used
    static {
        TomcatURLStreamHandlerFactory.getInstance();
    }

    @Override public XsltConverter<S, T> create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        XsltConverter<S,T> xsltConverter = new XsltConverter<>(TransformerFactoryImpl.newInstance());
        xsltConverter.setConfiguration(configuration(configPrefix,XsltConverterConfiguration.class));
        xsltConverter.setConfiguredResourceId(configuredResourceId(nameSuffix));
        return xsltConverter;
    }
}
