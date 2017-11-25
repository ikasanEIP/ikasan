package org.ikasan.dashboard.ui.control.util;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by stewmi on 20/11/2017.
 */
public class ModuleControlRunnable implements Runnable
{
    private Logger logger = Logger.getLogger(ModuleControlRunnable.class);

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
    public void run()
    {
        actionFlow();
    }

    protected boolean actionFlow()
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

            String responseMessage = response.readEntity(String.class);

            Notification.show(responseMessage, Notification.Type.ERROR_MESSAGE);

            String state = this.getFlowState(flow, authentication.getName(), (String)authentication.getCredentials());

            String key = flow.getModule().getName() + "-" + flow.getName();

            this.flowStates.put(key, state);

            updateTable(state);

            return false;
        }

        return true;
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
//        UI.getCurrent().access(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                VaadinSession.getCurrent().getLockInstance().lock();
//                try
//                {
                    item.getItemProperty("Status").setValue(state);

                    moduleTable.markAsDirty();
//                }
//                finally
//                {
//                    VaadinSession.getCurrent().getLockInstance().unlock();
//                }
//
//                UI.getCurrent().push();
//            }
//        });
    }
}
