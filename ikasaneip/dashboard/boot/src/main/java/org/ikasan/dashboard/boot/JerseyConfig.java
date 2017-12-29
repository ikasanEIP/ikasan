package org.ikasan.dashboard.boot;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import java.util.stream.Collectors;
import javax.ws.rs.ext.Provider;

/**
 * Created by Ikasan Development Team on 20/11/2017.
 */
@Configuration
@ApplicationPath("/rest")
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));

        registerClasses(scanner.findCandidateComponents("org.ikasan.dashboard.ui.monitor.rest").stream()
                .map(beanDefinition -> ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), getClassLoader()))
                .collect(Collectors.toSet()));

        registerClasses(scanner.findCandidateComponents("org.ikasan.dashboard.configurationManagement.rest").stream()
                .map(beanDefinition -> ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), getClassLoader()))
                .collect(Collectors.toSet()));
    }
}
