package org.ikasan.dashboard.ui.control.component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.IkasanUI;
import org.ikasan.dashboard.ui.control.util.FlowActions;
import org.ikasan.dashboard.ui.control.util.FlowStates;
import org.ikasan.dashboard.ui.control.design.ModuleControlDesign;
import org.ikasan.dashboard.ui.control.util.ModuleControlActionHelper;
import org.ikasan.dashboard.ui.control.util.ModuleControlRunnable;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.event.FlowStateEvent;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.topology.util.TopologyTreeActionHelper;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Ikasan Development Team on 09/11/2017.
 */
public class ModuleControlLayout extends ModuleControlDesign
{
    private Logger logger = LoggerFactory.getLogger(ModuleControlLayout.class);

    private TopologyService topologyService;

    private IndexedContainer container;

    private FilterTable moduleTable;

    private ModuleControlActionHelper moduleControlActionHelper;

    private ConcurrentHashMap<String, String> flowStates = new ConcurrentHashMap<String, String>();

    private TopologyStateCache topologyCache;

    private  ExecutorService executorService = Executors.newFixedThreadPool(20);

    private Button selectAllButton;
    private Button startButton;
    private Button stopButton;
    private Button pauseButton;

    private IkasanAuthentication authentication;

    public ModuleControlLayout(TopologyService topologyService, TopologyStateCache topologyCache)
    {
        super();
        this.topologyService = topologyService;
        if(this.topologyService == null)
        {
            throw new IllegalArgumentException("topology service cannot be null!");
        }
        this.topologyCache = topologyCache;
        if(this.topologyCache == null)
        {
            throw new IllegalArgumentException("topologyCache service cannot be null!");
        }

        init();
    }

    private void init()
    {
        EventBus eventBus = ((IkasanUI)UI.getCurrent()).getEventBus();
        eventBus.register(this);

        this.flowStates = this.topologyCache.getStateMap();

        this.container = buildContainer();

        selectAllButton = new Button();
        selectAllButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        selectAllButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
        selectAllButton.setImmediate(true);
        selectAllButton.setDescription("Select / deselect all records below.");

        selectAllButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(Button.ClickEvent event)
            {
                Collection<Flow> itemIds = (Collection<Flow>)container.getItemIds();

                Resource r = selectAllButton.getIcon();

                if(r.equals(VaadinIcons.CHECK_SQUARE_O))
                {
                    selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE);

                    for(Flow eo: itemIds)
                    {
                        Item item = container.getItem(eo);

                        CheckBox cb = (CheckBox)item.getItemProperty("").getValue();

                        cb.setValue(true);
                    }
                }
                else
                {
                    selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);

                    for(Flow eo: itemIds)
                    {
                        Item item = container.getItem(eo);

                        CheckBox cb = (CheckBox)item.getItemProperty("").getValue();

                        cb.setValue(false);
                    }
                }
            }
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth("100px");

        startButton = new Button();
        startButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        startButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        startButton.setIcon(VaadinIcons.PLAY);
        startButton.setImmediate(true);
        startButton.setDescription("Start all selected flows below.");

        startButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent)
            {
                actionFlows(FlowActions.START);
            }
        });

        buttonsLayout.addComponent(startButton);

        pauseButton = new Button();
        pauseButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        pauseButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        pauseButton.setIcon(VaadinIcons.PAUSE);
        pauseButton.setImmediate(true);
        pauseButton.setDescription("Pause all selected flows below.");

        pauseButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent)
            {
                actionFlows(FlowActions.PAUSE);
            }
        });

        buttonsLayout.addComponent(pauseButton);

        stopButton = new Button();
        stopButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        stopButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        stopButton.setIcon(VaadinIcons.STOP);
        stopButton.setImmediate(true);
        stopButton.setDescription("Stop all selected flows below.");

        stopButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent)
            {
                actionFlows(FlowActions.STOP);
            }
        });

        buttonsLayout.addComponent(stopButton);

        buttonsLayout.addComponent(selectAllButton);

        ((Layout)super.splitPanel.getFirstComponent()).addComponent(buttonsLayout);
        super.splitPanel.getFirstComponent().setWidth("100%");
        ((HorizontalLayout)super.splitPanel.getFirstComponent()).setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);

        this.moduleTable = new FilterTable();
        this.moduleTable.setFilterBarVisible(true);
        this.moduleTable.addStyleName(ValoTheme.TABLE_SMALL);
        this.moduleTable.addStyleName("ikasan");
        this.moduleTable.setSizeFull();
        this.moduleTable.setContainerDataSource(container);

        this.moduleTable.addActionHandler(new Action.Handler()
        {
            @Override
            public Action[] getActions(Object itemId, Object propertyId)
            {
                Flow flow = (Flow)itemId;

                String state = null;

                if(flow != null)
                {
                    String key = flow.getModule().getName() + "-" + flow.getName();

                    state = flowStates.get(key);
                }

                return moduleControlActionHelper.getFlowActions(state, true);
            }

            @Override
            public void handleAction(Action action, Object o, Object flow)
            {

                Item item = container.getItem(flow);

                ArrayList<Callable<String>> callables = new ArrayList<>();

                ModuleControlRunnable moduleControlRunnable = new ModuleControlRunnable(authentication, (Flow)flow, item, flowStates
                    , action.getCaption(), moduleTable);

                callables.add(moduleControlRunnable);

                try
                {
                    List<Future<String>> futures = executorService.invokeAll(callables);

                    StringBuffer result = new StringBuffer();

                    for(Future<String> future: futures)
                    {
                        result.append(future.get()).append("</br>");
                    }

                    Notification notification = new Notification("Flow Control Notification", result.toString(), Notification.Type.TRAY_NOTIFICATION, true);

                    notification.show(Page.getCurrent());
                }
                catch (Exception e)
                {
                    Notification.show("An error occurred performing flow actions: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        this.moduleTable.setCellStyleGenerator(new CustomTable.CellStyleGenerator()
        {
            @Override
            public String getStyle(CustomTable customTable, Object itemId, Object propertyId)
            {
                Flow flow = (Flow)itemId;

                String key = flow.getModule().getName() + "-" + flow.getName();

                String state = flowStates.get(key);

                logger.debug("propertyId: " + propertyId);

                if(state != null && propertyId != null && ((String)propertyId).equals("Status"))
                {
                    if(state.equals(FlowStates.RUNNING))
                    {
                        return "green";
                    }
                    else if(state.equals(FlowStates.STOPPED))
                    {
                        return "blue";
                    }
                    else if(state.equals(FlowStates.STOPPED_IN_ERROR))
                    {
                        return "red";
                    }
                    else if(state.equals(FlowStates.RECOVERING))
                    {
                        return "orange";
                    }
                    else if(state.equals(FlowStates.PAUSED))
                    {
                        return "purple";
                    }
                }

                return "white";
            }
        });

        super.splitPanel.setSecondComponent(this.moduleTable);
        super.setMargin(true);

        this.setSizeFull();
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Module", String.class,  null);
        cont.addContainerProperty("Flow", String.class,  null);
        cont.addContainerProperty("Status", String.class,  null);
        cont.addContainerProperty("", CheckBox.class,  null);

        return cont;
    }


    public void loadTable()
    {
        authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        if(authentication.hasGrantedAuthority(SecurityConstants.TOPOLOGY_ADMIN)
            || authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
        {
            this.startButton.setVisible(true);
            this.stopButton.setVisible(true);
            this.pauseButton.setVisible(true);
            this.selectAllButton.setVisible(true);
        }
        else
        {
            this.startButton.setVisible(false);
            this.stopButton.setVisible(false);
            this.pauseButton.setVisible(false);
            this.selectAllButton.setVisible(false);
        }

        this.moduleControlActionHelper = new ModuleControlActionHelper(authentication);

        container.removeAllItems();
        selectAllButton.setIcon(VaadinIcons.CHECK_SQUARE_O);
        
        List<Module> modules = this.topologyService.getAllModules();

        for(Module module:modules)
        {
            for(Flow flow:module.getFlows())
            {
                Item item = container.addItem(flow);

                item.getItemProperty("Module").setValue(module.getName());
                item.getItemProperty("Flow").setValue(flow.getName());


                String key = flow.getModule().getName() + "-" + flow.getName();

                String state = flowStates.get(key);

                item.getItemProperty("Status").setValue(state);

                CheckBox cb = new CheckBox();
                cb.setImmediate(true);
                cb.setDescription("Select in order perform action.");

                item.getItemProperty("").setValue(cb);
            }
        }
    }

    protected void actionFlows(String action)
    {
        final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                .getAttribute(DashboardSessionValueConstants.USER);

        ArrayList<Callable<String>> callables = new ArrayList<>();

        for(Flow flow: (List<Flow>)container.getItemIds())
        {
            Item item = container.getItem(flow);

            boolean checked = ((CheckBox)item.getItemProperty("").getValue()).getValue();

            if(checked)
            {
                ModuleControlRunnable moduleControlRunnable = new ModuleControlRunnable(authentication, flow, item, flowStates, action, this.moduleTable);

                callables.add(moduleControlRunnable);
            }
        }

        try
        {
            List<Future<String>> futures = this.executorService.invokeAll(callables);

            StringBuffer result = new StringBuffer();

            for(Future<String> future: futures)
            {
                result.append(future.get()).append("</br>");
            }

            Notification notification = new Notification("Flow Control Notification", result.toString(), Notification.Type.TRAY_NOTIFICATION, true);

            notification.show(Page.getCurrent());
        }
        catch (Exception e)
        {
            Notification.show("An error occurred performing flow actions: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Subscribe
    public void receiveFlowStateEvent(final FlowStateEvent event)
    {
        logger.debug("Received broadcast!" + event);
        UI.getCurrent().access(new Runnable()
        {
            @Override
            public void run()
            {
                VaadinSession.getCurrent().getLockInstance().lock();
                try
                {
                    // module.getName() + "-" + flow.getName()
                    ConcurrentHashMap<String, String> states = event.getFlowStateMap();

                    for(String flowKey: states.keySet())
                    {
                        logger.debug("Updating state! Flow[" + flowKey + "] State [" + states.get(flowKey) + "]");
                        flowStates.put(flowKey, states.get(flowKey));
                    }

                    for(Flow flow: (List<Flow>)container.getItemIds())
                    {
                        Item item = container.getItem(flow);

                        String key = flow.getModule().getName() + "-" + flow.getName();
                        item.getItemProperty("Status").setValue(flowStates.get(key));
                    }

                    moduleTable.markAsDirty();
                }
                finally
                {
                    VaadinSession.getCurrent().getLockInstance().unlock();
                }

                UI.getCurrent().push();
            }
        });
    }
}
