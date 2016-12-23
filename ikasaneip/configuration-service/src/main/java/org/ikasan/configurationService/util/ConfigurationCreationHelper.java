package org.ikasan.configurationService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
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
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
    private PlatformConfigurationService platformConfigurationService;

    public ConfigurationCreationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
                                       PlatformConfigurationService platformConfigurationService)
    {
        this.configurationManagement = configurationManagement;
        if(this.configurationManagement == null)
        {
            throw new IllegalArgumentException("configurationManagement cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
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

                String username = platformConfigurationService.getWebServiceUsername();
        String password = platformConfigurationService.getWebServicePassword();

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        Client client = ClientBuilder.newClient(clientConfig);

        ObjectMapper mapper = new ObjectMapper();

        WebTarget webTarget = client.target(url);

        Response response = webTarget.request().get();

        if (response.getStatus() != 200) {
            response.bufferEntity();

            String responseMessage = response.readEntity(String.class);
            throw new RuntimeException("An error was received trying to create configured resource '" + component.getConfigurationId() + "': "
                    + responseMessage);
        }

        return this.configurationManagement.getConfiguration(component.getConfigurationId());
    }
}
