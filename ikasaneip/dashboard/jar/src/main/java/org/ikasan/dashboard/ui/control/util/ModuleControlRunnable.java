package org.ikasan.dashboard.ui.control.util;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.tepi.filtertable.FilterTable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ikasan Development Team on 20/11/2017.
 */
public class ModuleControlRunnable implements Runnable, Callable<String>
{
    private Logger logger = LoggerFactory.getLogger(ModuleControlRunnable.class);

    private IkasanAuthentication authentication;
    private Flow flow;
    private ConcurrentHashMap<String, String> flowStates;
    private String action;
    private Item item;
    private FilterTable moduleTable;

    public ModuleControlRunnable(IkasanAuthentication authentication, Flow flow, Item item, ConcurrentHashMap<String, String> flowStates, String action,
                                  FilterTable moduleTable)
    {
        this.authentication = authentication;
        this.flow = flow;
        this.flowStates = flowStates;
        this.action = action;
        this.item = item;
        this.moduleTable = moduleTable;
    }

    @Override
    public String call() throws Exception
    {
        return actionFlow();
    }

    @Override
    public void run()
    {
        actionFlow();
    }

    protected String actionFlow()
    {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authentication.getName(), (String)authentication.getCredentials());

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature) ;

        Client client = ClientBuilder.newClient(clientConfig);

        String url = flow.getModule().getServer().getUrl() + ":" + flow.getModule().getServer().getPort()
                + flow.getModule().getContextRoot()
                + "/rest/moduleControl/controlFlowState/"
                + flow.getModule().getName()
                + "/"
                + flow.getName();

        logger.info("User " + authentication.getName() + " attempting to " + action + " flow " + flow + " url: " + url);
        WebTarget webTarget = client.target(url);
        Response response = webTarget.request().put(Entity.entity(action, MediaType.APPLICATION_OCTET_STREAM));

        logger.info("Response: " + response);

        if(response.getStatus()  == 200)
        {
            String key = flow.getModule().getName() + "-" + flow.getName();

            String state = this.getFlowState(flow, authentication.getName(), (String)authentication.getCredentials());

            this.flowStates.put(key, state);

            updateTable(state);
        }
        else
        {
            response.bufferEntity();

            String state = this.getFlowState(flow, authentication.getName(), (String)authentication.getCredentials());

            String key = flow.getModule().getName() + "-" + flow.getName();

            this.flowStates.put(key, state);

            updateTable(state);

            return String.format("Action[<b>%s</b>] Flow[<b>%s</b>] Status[<font color=\"red\">ERROR</font>]", action, flow.getModule().getName() + "-" + flow.getName());
        }

        return String.format("Action[<b>%s</b>] Flow[<b>%s</b>] Status[<font color=\"green\">SUCCESS</font>]", action, flow.getModule().getName() + "-" + flow.getName());
    }

    protected String getFlowState(Flow flow, String username, String password)
    {
        String result;
        String url = null;

        try
        {
            url = flow.getModule().getServer().getUrl() + ":" + flow.getModule().getServer().getPort()
                    + flow.getModule().getContextRoot()
                    + "/rest/moduleControl/flowState/"
                    + flow.getModule().getName() + "/"
                    + flow.getName();

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature) ;

            Client client = ClientBuilder.newClient(clientConfig);

            logger.info("Calling URL: " + url);
            WebTarget webTarget = client.target(url);

            result = webTarget.request().get(String.class);

            logger.info("Result: " + result);
        }
        catch(Exception e)
        {
            logger.debug("Caught exception attempting to discover module with the following URL: " + url
                    + ". Ignoring and moving on to next module. Exception message: " + e.getMessage());

            return "";
        }

        return result;
    }

    public void updateTable(String state)
    {
        item.getItemProperty("Status").setValue(state);

        moduleTable.markAsDirty();
    }
}
