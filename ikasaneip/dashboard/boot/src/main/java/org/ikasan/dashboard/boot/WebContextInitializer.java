package org.ikasan.dashboard.boot;

import org.ikasan.dashboard.ui.WebAppStartStopListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
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
        servletContext.setInitParameter("resteasy.scan", "false");
        servletContext.setInitParameter("resteasy.scan.providers", "false");
        servletContext.setInitParameter("resteasy.scan.resources", "false");
        servletContext.setInitParameter("heartbeatInterval", "300");
        servletContext.setInitParameter("productionMode", "true");

        registerServlet(servletContext);
    }

    /**
     * <servlet>
     <servlet-name>Ikasan Dashboard</servlet-name>
     <servlet-class>ru.xpoft.vaadin.SpringVaadinServlet</servlet-class>
     <initCloud-param>
     <param-name>UIProvider</param-name>
     <param-value>org.ikasan.dashboard.ui.DashboardUIProvider</param-value>
     </initCloud-param>
     <initCloud-param>
     <param-name>systemMessagesBeanName</param-name>
     <param-value>DEFAULT</param-value>
     </initCloud-param>
     <initCloud-param>
     <param-name>widgetset</param-name>
     <param-value>org.ikasan.dashboard.ui.AppWidgetSet</param-value>
     </initCloud-param>
     <initCloud-param>
     <param-name>closeIdleSessions</param-name>
     <param-value>true</param-value>
     </initCloud-param>
     <initCloud-param>
     <param-name>legacyPropertyToString</param-name>
     <param-value>true</param-value>
     </initCloud-param>
     <async-supported>true</async-supported>
     </servlet>
     * @param servletContext
     */
    private void registerServlet(ServletContext servletContext) {
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "Ikasan Dashboard", SpringVaadinServlet.class);

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*","/VAADIN/*", "/static/*");
//        dispatcher.addMapping("/VAADIN/*", "/static/*");
        dispatcher.setAsyncSupported(true);
        dispatcher.setInitParameter("legacyPropertyToString","true");
        dispatcher.setInitParameter("closeIdleSessions","true");
        dispatcher.setInitParameter("widgetset","org.ikasan.dashboard.ui.AppWidgetSet");
        dispatcher.setInitParameter("UIProvider","org.ikasan.dashboard.ui.DashboardUIProvider");

//        ServletRegistration.Dynamic jerseyDispatcher = servletContext.addServlet(
//                "jersey-servlet", org.glassfish.jersey.servlet.ServletContainer.class);
//
//        jerseyDispatcher.setLoadOnStartup(1);
//        jerseyDispatcher.addMapping("/rest/*");
//        jerseyDispatcher.setAsyncSupported(true);
//        jerseyDispatcher.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature","true");
    }

}