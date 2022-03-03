package org.ikasan.ootb.scheduler.agent.module.pointcut;

import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = "classpath:/META-INF/aop.xml")
public class AspectConfig {

    @Bean
    public FileMessageProviderAspect fileMessageProviderAspect() {
        return Aspects.aspectOf(FileMessageProviderAspect.class);
    }
}
