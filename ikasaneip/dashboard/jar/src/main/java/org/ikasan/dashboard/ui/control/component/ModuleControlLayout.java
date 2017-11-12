package org.ikasan.dashboard.ui.control.component;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.ui.control.design.ModuleControlDesign;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.tepi.filtertable.FilterTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stewmi on 09/11/2017.
 */
public class ModuleControlLayout extends ModuleControlDesign
{
    private TopologyService topologyService;

    private SimpleStringFilter filter = null;
    private Container.Filterable f = null;

    private IndexedContainer container;

    private FilterTable moduleTable;

    public ModuleControlLayout(TopologyService topologyService)
    {
        super();
        this.topologyService = topologyService;

        init();
    }

    private void init()
    {
        this.container = buildContainer();

        this.moduleTable = new FilterTable();
        this.moduleTable.setFilterBarVisible(true);
        this.moduleTable.addStyleName(ValoTheme.TABLE_SMALL);
        this.moduleTable.addStyleName("ikasan");
        this.moduleTable.setSizeFull();
        this.moduleTable.setContainerDataSource(container);

        super.splitPanel.setSecondComponent(this.moduleTable);

        this.setSizeFull();
    }

    protected IndexedContainer buildContainer()
    {
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Module", String.class,  null);
        cont.addContainerProperty("Flow", String.class,  null);
        cont.addContainerProperty("Status", Button.class,  null);

        return cont;
    }


    public void loadTable()
    {
        container.removeAllItems();
        
        List<Module> modules = this.topologyService.getAllModules();

        for(Module module:modules)
        {
            for(Flow flow:module.getFlows())
            {
                Item item = container.addItem(flow);

                item.getItemProperty("Module").setValue(module.getName());
                item.getItemProperty("Flow").setValue(flow.getName());


                Button icon = new Button();
                icon.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                icon.addStyleName(ValoTheme.BUTTON_BORDERLESS);
                icon.setIcon(FontAwesome.CHECK_CIRCLE);
                icon.addStyleName("green");
                icon.setDescription("Running");


                item.getItemProperty("Status").setValue(icon);
            }
        }
    }
}
