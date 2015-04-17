package org.ikasan.dashboard.ui.framework.tab;


import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationSearchResultsPanel;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.NewActionsPanel;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class HomeTab extends VerticalLayout implements View
{
    private static final long serialVersionUID = -4759498822589839343L;
    private MappingConfigurationSearchPanel searchPanel;
    private MappingConfigurationSearchResultsPanel searchResultsPanel;
    private ViewComponentContainer viewComponentContainer;
    private NewActionsPanel newActionsPanel;
    private HorizontalSplitPanel horizontalSplitPanel;
    private Panel leftPanelLayout;

    /**
     * Constructor
     * 
     * @param searchPanel
     * @param searchResultsPanel
     * @param viewComponentContainer
     * @param newActionsPanel
     */
    public HomeTab(MappingConfigurationSearchPanel searchPanel,
            MappingConfigurationSearchResultsPanel searchResultsPanel,
            ViewComponentContainer viewComponentContainer,
            NewActionsPanel newActionsPanel)
    {
        this.searchPanel = searchPanel;
        this.searchResultsPanel = searchResultsPanel;
        this.viewComponentContainer = viewComponentContainer;
        this.newActionsPanel = newActionsPanel;

        this.init();
    }

    /**
     * Helper method to initialise this object.
     */
    protected void init() {
        this.setSizeFull();
        Panel panel = new Panel();
        panel.setSizeFull();

        this.leftPanelLayout = getExpandedLeftSplitPanelLayout();
        this.horizontalSplitPanel 
            = new HorizontalSplitPanel(this.leftPanelLayout, viewComponentContainer);
        this.horizontalSplitPanel.setSizeFull();
        this.horizontalSplitPanel.setSplitPosition(320, Unit.PIXELS);
        this.horizontalSplitPanel.setLocked(true);
        this.horizontalSplitPanel.addStyleName("ikasansplitpanel");
        panel.setContent(horizontalSplitPanel);
        super.addComponent(panel);
    }

    /**
     * Setup the layout for the left panel.
     * 
     * @return
     */
    private Panel getExpandedLeftSplitPanelLayout()
    {   
    	
        Button collapseButton = new Button("<<");
        collapseButton.setVisible(true);
        collapseButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                horizontalSplitPanel.setLocked(false);
                horizontalSplitPanel.setSplitPosition(30, Unit.PIXELS);
                horizontalSplitPanel.setLocked(true);
                horizontalSplitPanel.setFirstComponent(getCollapsedLeftSplitPanelLayout());
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(collapseButton);
        collapseButton.setHeight(15, Unit.PIXELS);
        collapseButton.setWidth(15, Unit.PIXELS);
        collapseButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.setComponentAlignment(collapseButton, Alignment.TOP_RIGHT);
        layout.addComponent(searchPanel);
        layout.setComponentAlignment(searchPanel, Alignment.TOP_RIGHT);
        layout.addComponent(newActionsPanel);
        layout.setComponentAlignment(newActionsPanel, Alignment.TOP_RIGHT);

        layout.setStyleName("grey");
        
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName("dashboard-grey");
        panel.setContent(layout);
        
        return panel;
    }

    private Panel getCollapsedLeftSplitPanelLayout()
    {   
        Button expandButton = new Button(">>");
        expandButton.setVisible(true);
        expandButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                horizontalSplitPanel.setLocked(false);
                horizontalSplitPanel.setSplitPosition(320, Unit.PIXELS);
                horizontalSplitPanel.setLocked(true);
                horizontalSplitPanel.setFirstComponent(getExpandedLeftSplitPanelLayout());
            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(expandButton);
        expandButton.setHeight(15, Unit.PIXELS);
        expandButton.setWidth(15, Unit.PIXELS);
        layout.setComponentAlignment(expandButton, Alignment.TOP_CENTER);
        expandButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.setStyleName("grey");
        layout.setHeight("100%");

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName("dashboard-grey");
        panel.setContent(layout);
        
        return panel;
    }

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
