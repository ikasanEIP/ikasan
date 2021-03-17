package com.ikasan.component.factory;

import org.ikasan.builder.BuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


public class JmsComponentFactory
{
    @Resource
    protected BuilderFactory builderFactory;

    @Value("${module.name}")
    protected String moduleName;

    @Autowired
    protected Environment env;

    protected Map<String,String> getJndiProperties(String clientId, String prefix, boolean useClientIdPrefix){
        Map<String,String> properties = new HashMap<>();
        properties.put("java.naming.factory.initial", env.getProperty( prefix + ".java.naming.factory.initial"));
        properties.put("java.naming.security.principal", env.getProperty( prefix + ".connectionFactoryUsername"));
        properties.put("java.naming.security.credentials", env.getProperty( prefix + ".connectionFactoryPassword"));
        properties.put("java.naming.provider.url", getBroker(env.getProperty( prefix + ".provider.url"), clientId, useClientIdPrefix));
        return properties;
    }

    protected String getBroker(String brokerUrl, String clientId, boolean useClientIdPrefix) {
        if (brokerUrl.startsWith("vm") || brokerUrl.startsWith("remote") || brokerUrl.startsWith("jnp")){
            return brokerUrl;
        }
        else {
            String symbol;
            if (brokerUrl.contains("?")){
                symbol = "&";
            } else {
                symbol = "?";
            }
            if (useClientIdPrefix){
                return brokerUrl + symbol + "jms.clientIDPrefix" + clientId;
            } else {
                return brokerUrl + symbol + "jms.clientID=" + clientId;
            }
        }
    }
}
