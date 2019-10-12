package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import elemental.json.JsonArray;
import org.ikasan.dashboard.ui.component.ErrorListDialog;
import org.ikasan.dashboard.ui.component.EventViewDialog;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.component.WiretapListDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.component.ControlPanel;
import org.ikasan.dashboard.ui.visualisation.component.ModuleVisualisation;
import org.ikasan.dashboard.ui.visualisation.dao.BusinessStreamMetaDataDaoImpl;
import org.ikasan.dashboard.ui.visualisation.dao.ModuleMetaDataDaoImpl;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
import org.ikasan.vaadin.visjs.network.listener.ClickListener;
import org.ikasan.vaadin.visjs.network.listener.DoubleClickListener;
import org.ikasan.vaadin.visjs.network.listener.OnContextListener;
import org.ikasan.vaadin.visjs.network.options.Interaction;
import org.ikasan.vaadin.visjs.network.options.Options;
import org.ikasan.vaadin.visjs.network.options.edges.ArrowHead;
import org.ikasan.vaadin.visjs.network.options.edges.Arrows;
import org.ikasan.vaadin.visjs.network.options.edges.EdgeColor;
import org.ikasan.vaadin.visjs.network.options.edges.Edges;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.options.physics.Physics;
import org.ikasan.vaadin.visjs.network.util.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.erik.SlideMode;
import org.vaadin.erik.SlideTab;
import org.vaadin.erik.SlideTabBuilder;
import org.vaadin.erik.SlideTabPosition;
import org.vaadin.tabs.PagedTabs;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Route(value = "module", layout = IkasanAppLayout.class)
@UIScope
@Component
public class ModuleView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(ModuleView.class);

    @Resource
    private WiretapService<FlowEvent,PagedSearchResult<WiretapEvent>> solrWiretapService;

    @Resource
    private ErrorReportingService solrErrorReportingService;

    @Resource
    private ExclusionManagementService solrExclusionService;

    @Autowired
    private ModuleMetaDataService moduleMetadataService;

    @Resource
    private ConfigurationMetaDataService configurationMetadataService;

    private VaadinSession session;
    private UI current;
    private Grid<ModuleMetaData> modulesGrid = new Grid<>();
    private ModuleMetaDataDaoImpl moduleMetaDataDao = new ModuleMetaDataDaoImpl();
    private H2 moduleLabel = new H2();
    private HorizontalLayout hl = new HorizontalLayout();

    /**
     * Constructor
     */
    public ModuleView()
    {
        this.setMargin(true);
        this.setSizeFull();


        this.createModuleGrid();

        session = UI.getCurrent().getSession();
        current = UI.getCurrent();
    }

    protected void createModuleGrid()
    {
        // Create a modulesGrid bound to the list
        modulesGrid.removeAllColumns();
        modulesGrid.setVisible(true);
        modulesGrid.setSizeFull();

        modulesGrid.addColumn(ModuleMetaData::getName).setHeader("Name");
        modulesGrid.addColumn(ModuleMetaData::getName).setHeader("Status");
        modulesGrid.addColumn(new ComponentRenderer<>((ModuleMetaData node) ->
        {
            Button view = new Button(VaadinIcon.EYE.create());
            view.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
            {
                this.moduleLabel.setText(node.getName());
                this.hl.setVisible(true);
            });

            return view;

        }));

        modulesGrid.setItemDetailsRenderer(
            new ComponentRenderer<>(moduleMetaData -> {
                Grid<FlowMetaData> grid = this.createFlowGrid();
                grid.setItems(moduleMetaData.getFlows());

                VerticalLayout layout = new VerticalLayout();
                layout.setWidth("100%");
                layout.setHeight("200px");
                layout.add(grid);

                return layout;
            }));

        this.add(modulesGrid);
    }

    protected Grid createFlowGrid()
    {
        // Create a modulesGrid bound to the list
        Grid<FlowMetaData> flowGrid = new Grid<>();
        flowGrid.removeAllColumns();
        flowGrid.setVisible(true);
        flowGrid.setSizeFull();

        flowGrid.addColumn(FlowMetaData::getName).setHeader("Name");
        flowGrid.addColumn(new ComponentRenderer<>((FlowMetaData node) ->
        {
            Icon icon = new Icon(VaadinIcon.CIRCLE);
            icon.setColor("green");
            icon.setSize("20px");

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSizeFull();

            H6 statusLabel = new H6("Running");
            layout.add(icon, statusLabel);

            layout.setVerticalComponentAlignment(Alignment.CENTER, icon, statusLabel);

            return layout;
        })).setHeader("Status").setKey("status");
        flowGrid.addColumn(new ComponentRenderer<>((FlowMetaData node) ->
        {
            ControlPanel controlPanel = new ControlPanel(null);

            return controlPanel;

        }));

        flowGrid.getColumnByKey("status").setClassNameGenerator(item -> {
            return "success";
        });

        return flowGrid;
    }



    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.populateModulesGrid();
    }


    /**
     * Method to initialise the modulesGrid on the tools slider.
     */
    protected void populateModulesGrid()
    {
        // todo this is a temp hack to use the dao tp provide data.
        List<ModuleMetaData> moduleMetaData = moduleMetadataService.findAll();
        moduleMetaData.addAll(this.moduleMetaDataDao.getAllModule());
        modulesGrid.setItems(moduleMetaData);
    }


}

