package org.ikasan.dashboard.configurationManagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by stewmi on 20/12/2016.
 */
public class ConfigurationCreationHelper
{
    ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    public ConfigurationCreationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
        this.configurationManagement = configurationManagement;
    }

    public Configuration createConfiguration(Component component)
    {
        Server server = component.getFlow().getModule().getServer();

        String url = server.getUrl() + ":" + server.getPort()
                + component.getFlow().getModule().getContextRoot()
                + "/rest/configuration/createConfiguration/"
                + component.getFlow().getModule().getName()
                + "/"
                + component.getFlow().getName()
                + "/"
                + component.getName();


        IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String) authentication.getCredentials());

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        Client client = ClientBuilder.newClient(clientConfig);

        ObjectMapper mapper = new ObjectMapper();

        WebTarget webTarget = client.target(url);

        Response response = webTarget.request().get();

        if (response.getStatus() != 200) {
            response.bufferEntity();

            String responseMessage = response.readEntity(String.class);
            Notification.show("An error was received trying to create configured resource '" + component.getConfigurationId() + "': "
                    + responseMessage, Notification.Type.ERROR_MESSAGE);

            return null;
        }

        return this.configurationManagement.getConfiguration(component.getConfigurationId());
    }
}
