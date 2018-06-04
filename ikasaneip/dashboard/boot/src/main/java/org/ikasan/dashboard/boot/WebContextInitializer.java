package org.ikasan.dashboard.boot;

import org.ikasan.dashboard.ui.WebAppStartStopListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
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
        dispatcher.addMapping("/*","/VAADIN/*");
        dispatcher.setAsyncSupported(true);
        dispatcher.setInitParameter("legacyPropertyToString","true");
        dispatcher.setInitParameter("closeIdleSessions","true");
        dispatcher.setInitParameter("widgetset","org.ikasan.dashboard.ui.AppWidgetSet");
        dispatcher.setInitParameter("UIProvider","org.ikasan.dashboard.ui.DashboardUIProvider");

    }

}