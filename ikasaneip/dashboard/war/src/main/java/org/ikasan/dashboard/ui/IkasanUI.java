package org.ikasan.dashboard.ui;

import java.util.List;

import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.dashboard.ui.framework.tab.HomeTab;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.security.service.UserService;

import com.mizuho.cmi2.security.service.SecurityService;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
/**
 * 
 * @author CMI2 Development Team
 *
 */
public class IkasanUI extends UI
{   
    private HomeTab homeTab;
    private List<IkasanUIView> views;
    private ViewComponentContainer viewComponentContainer;
    private UserService userService;
    private SecurityService securityService;
    private VisibilityGroup visibilityGroup;
    private UserDetailsHelper userDetailsHelper;
    private EditableGroup editableGroup;
    private FunctionalGroup newMappingConfigurationFunctionalGroup;
    private FunctionalGroup existingMappingConfigurationFunctionalGroup;
    private VerticalLayout imagePanelLayout;

    /**
     * Constructor
     * 
     * @param homeTab
     * @param views
     * @param viewComponentContainer
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param userDetailsHelper
     * @param editableGroup
     * @param newMappingConfigurationFunctionalGroup
     * @param existingMappingConfigurationFunctionalGroup
     */
	public IkasanUI(HomeTab homeTab, List<IkasanUIView> views,
	        ViewComponentContainer viewComponentContainer, UserService userService,
	        SecurityService securityService, VisibilityGroup visibilityGroup,
            UserDetailsHelper userDetailsHelper, EditableGroup editableGroup,
            FunctionalGroup newMappingConfigurationFunctionalGroup, FunctionalGroup existingMappingConfigurationFunctionalGroup)
	{
	    this.homeTab = homeTab;
	    this.views = views;
	    this.userService = userService;
	    this.securityService = securityService;
	    this.visibilityGroup = visibilityGroup;
	    this.viewComponentContainer = viewComponentContainer;
	    this.userDetailsHelper = userDetailsHelper;
	    this.editableGroup = editableGroup;
	    this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
	    this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
	}

    @Override
    protected void init(VaadinRequest request) {
        final GridLayout layout = new GridLayout(1, 3);	
        layout.setSizeFull();   
        layout.setMargin(true);
        this.setContent(layout);

        imagePanelLayout = new VerticalLayout();
        imagePanelLayout.setMargin(true);
        imagePanelLayout.setHeight("140px");

        layout.addComponent(imagePanelLayout, 0, 0);

        imagePanelLayout.setStyleName("v-header");

        ThemeResource resource = new ThemeResource("images/Ikasan_Logo_Transp.png");
        Image image = new Image("", resource);
        imagePanelLayout.addComponent(image);
        image.setHeight("100%");
        imagePanelLayout.setExpandRatio(image, 0.8f);
        Label label = new Label("Enterprise Integration Platform - Mapping Configuration Service");
        label.setStyleName("ikasan-maroon");
        label.setHeight("100%");
        imagePanelLayout.addComponent(label);
        imagePanelLayout.setExpandRatio(label, 0.2f);
        imagePanelLayout.setComponentAlignment(label, Alignment.BOTTOM_LEFT);

        NavigationPanel navigationPanel = new NavigationPanel(this.userService, this.securityService
            , this.visibilityGroup, this.userDetailsHelper, this.editableGroup, this.newMappingConfigurationFunctionalGroup,
            this.existingMappingConfigurationFunctionalGroup, imagePanelLayout);
        layout.addComponent(navigationPanel, 0, 1);
        
        layout.addComponent(this.homeTab, 0, 2);
        layout.setRowExpandRatio(2, 1);

        Navigator navigator = new Navigator(this, this.viewComponentContainer);

        for(IkasanUIView view: views)
        {
            navigator.addView(view.getPath(), view.getView());
        }

        navigator.navigateTo("emptyPanel");
    }

}
