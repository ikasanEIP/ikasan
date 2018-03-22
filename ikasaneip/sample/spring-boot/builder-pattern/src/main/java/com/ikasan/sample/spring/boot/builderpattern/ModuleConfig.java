package com.ikasan.sample.spring.boot.builderpattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:ikasan-transaction-pointcut-resubmission.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:h2-datasource-conf.xml"
} )
public class ModuleConfig
{

}
