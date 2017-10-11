package org.ikasan.sample.spring.boot.builderpattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@ImportResource( {
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:ikasan-transaction-pointcut-resubmission.xml",
        "classpath:src/main/resources/ikasan-transaction-pointcut-quartz.xml"

} )
public class ModuleConfig
{

}
