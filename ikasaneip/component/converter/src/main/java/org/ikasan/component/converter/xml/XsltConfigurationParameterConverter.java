package org.ikasan.component.converter.xml;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Converts XsltConverter configuration parameters into xslt parameters.
 *
 * By convention fields in the configuration that start with xsltParam will be injected into
 * the transformer and made available to the xslt.
 *
 * e.g. a field with name xsltParamMyList, would have the xsltParam stripped and passed in as
 * a parameter named myList.
 *
 * Primitive fields will be injected as is. Map and List parameters will be converted into an xml fragment
 * as follows, before being injected into a parameter named as above:
 *
 * Map:
 *
 * <map>
 *  <entry key="key 1" value="1"/>
 *  <entry key="key 3" value="2"/>
 * </map>
 *  etc.
 *
 * List:
 * <list>
 *  <value>1</value>
 *  <value>2</value>
 * </list>
 *  etc.
 *
 *
 *
 * Created by elliga on 29/07/2015.
 *
 */
public class XsltConfigurationParameterConverter implements Converter<XsltConverterConfiguration, Map<String, String>> {

    private static final Logger logger = LoggerFactory.getLogger(XsltConfigurationParameterConverter.class);

    private static final String XSLT_PARAM_PREFIX = "xsltParam";

    @Override
    public Map<String, String> convert(XsltConverterConfiguration configuration) throws TransformationException {

        // Get fields that match the convention of starting with xsltParam.
        // These will be injected into the transformer
        List<Field> fieldsToInject = getFieldsToInject(configuration);

        Map<String, String> params = new HashMap<>();

        // convert each field into a format that can be used by the xslt transformer - primitives will be a straight pass through
        for (Field field : fieldsToInject) {
            try {
                convertField(params, field, configuration);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("error occurred introspecting XsltConverterConfiguration instance for field name: %s".formatted(field.getName()), e);
            }
        }

        return params;
    }

    private void convertField(Map<String, String> params, Field field, XsltConverterConfiguration configuration) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = field.getName();
        name = name.replace(XSLT_PARAM_PREFIX, "");
        name = Introspector.decapitalize(name);
        String value = convertValue(field, configuration);
        if (value != null) {
            params.put(name, value);
        }
    }

    private String convertValue(Field field, XsltConverterConfiguration configuration) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMethod;

        try {
            // try getting getter method - getXXX()
            getMethod = configuration.getClass().getMethod(convertFieldNameToGetMethod(field.getName()));
        } catch (NoSuchMethodException e) {

            // try getting the boolean equivalent - isXXX()
            try {
                getMethod = configuration.getClass().getMethod(convertFieldNameToIsMethod(field.getName()));
            } catch (NoSuchMethodException e2) {
                String errorMessage = "Unable to find getter (getXXX,isXXX) method for field: %s. Check the XsltConverterConfiguration has corresponding getter methods.".formatted(field.getName());
                throw new RuntimeException(errorMessage, e2);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Created get method: %s, for field: %s".formatted(getMethod.getName(), field.getName()));
        }

        Object result = getMethod.invoke(configuration);

        if (result == null) return null;

        if (result instanceof String string) {
            return string;
        } else if (result instanceof Integer integer) {
            return Integer.toString(integer);
        } else if (result instanceof Long long1) {
            return Long.toString(long1);
        } else if (result instanceof Boolean boolean1) {
            return Boolean.toString(boolean1);
        } else if (result instanceof Map) {
            return convertMapToXml((Map<String, String>) result);
        } else if (result instanceof List) {
            return convertListToXml((List<String>) result);
        } else {
            throw new RuntimeException("unsupported type, unable to convert: %s".formatted(result.getClass().getName()));
        }
    }

    private String convertListToXml(List<String> list) {
        StringBuilder builder = new StringBuilder();

        builder.append("<list>");
        for (String s : list) {
            builder.append("<value>").append(s).append( "</value>");
        }
        builder.append("</list>");

        return builder.toString();
    }

    private String convertMapToXml(Map<String, String> result) {
        StringBuilder builder = new StringBuilder();

        Set<String> keySet = result.keySet();

        builder.append("<map>");
        for (String key : keySet) {
            String value = result.get(key);
            builder.append("<entry key=\"%s\" value=\"%s\"/>".formatted(key, value));
        }
        builder.append("</map>");

        return builder.toString();
    }

    private String convertFieldNameToIsMethod(String name) {
        return "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String convertFieldNameToGetMethod(String name) {
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private List<Field> getFieldsToInject(XsltConverterConfiguration configuration) {
        Field[] fields = configuration.getClass().getDeclaredFields();
        List<Field> fieldsToInject = new ArrayList<>();

        for (Field field : fields) {
            String name = field.getName();

            if (field.getName().startsWith(XSLT_PARAM_PREFIX)) {
                fieldsToInject.add(field);
            }
        }

        return fieldsToInject;
    }
}
