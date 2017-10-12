package org.ikasan.dashboard.boot;

import org.ikasan.dashboard.ui.WebAppStartStopListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import ru.xpoft.vaadin.SpringVaadinServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by amajewski on 24/04/2017.
 */

@Configuration
public class WebContextInitializer implements ServletContextInitializer
{

    @Override
    public void onStartup(javax.servlet.ServletContext servletContext)
            throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        // alternatively, could use context.register(MyConfiguration.class) and
        // optionally @ComponentScan("my.package") on the configuration class
        //context.scan(WebContextInitializer.class.getPackage().getName());
        servletContext.addListener(new WebAppStartStopListener());
        registerServlet(servletContext);
    }

    /**
     * <servlet>
     <servlet-name>Ikasan Dashboard</servlet-name>
     <servlet-class>ru.xpoft.vaadin.SpringVaadinServlet</servlet-class>
     <init-param>
     <param-name>UIProvider</param-name>
     <param-value>org.ikasan.dashboard.ui.DashboardUIProvider</param-value>
     </init-param>
     <init-param>
     <param-name>systemMessagesBeanName</param-name>
     <param-value>DEFAULT</param-value>
     </init-param>
     <init-param>
     <param-name>widgetset</param-name>
     <param-value>org.ikasan.dashboard.ui.AppWidgetSet</param-value>
     </init-param>
     <init-param>
     <param-name>closeIdleSessions</param-name>
     <param-value>true</param-value>
     </init-param>
     <init-param>
     <param-name>legacyPropertyToString</param-name>
     <param-value>true</param-value>
     </init-param>
     <async-supported>true</async-supported>
     </servlet>
     * @param servletContext
     */
    private void registerServlet(ServletContext servletContext) {
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "vaadin", SpringVaadinServlet.class);

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*","/VAADIN/*");
        dispatcher.setAsyncSupported(true);
        dispatcher.setInitParameter("legacyPropertyToString","true");
        dispatcher.setInitParameter("closeIdleSessions","true");
        dispatcher.setInitParameter("widgetset","org.ikasan.dashboard.ui.AppWidgetSet");
        dispatcher.setInitParameter("UIProvider","org.ikasan.dashboard.ui.DashboardUIProvider");
    }

}