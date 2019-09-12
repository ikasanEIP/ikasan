package org.ikasan.dashboard.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {
    "classpath:providers-conf.xml",
    "classpath:configuration-service-conf.xml",
    "classpath:systemevent-service-conf.xml"
} )
public class PersistenceComponentFactory
{
}
