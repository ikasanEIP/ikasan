package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.vaadin.tabs.PagedTabs;

import javax.annotation.Resource;

@Route(value = "adminSearchView", layout = IkasanAppLayout.class)
@UIScope
@Component
@PageTitle("Ikasan - Administration Search")
public class AdministrationSearchView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(AdministrationSearchView.class);

    @Resource
    private SolrGeneralServiceImpl solrSearchService;

    private PagedTabs tabs;

    private SystemEventSearchView systemEventSearchView;

    /**
     * Constructor
     */
    public AdministrationSearchView()
    {
        super();
    }

    protected void init()
    {
        tabs = new PagedTabs();
        tabs.getElement().getThemeList().remove("padding");
        tabs.setSizeFull();

        this.systemEventSearchView = new SystemEventSearchView(this.solrSearchService);
        this.systemEventSearchView.init();
        this.systemEventSearchView.getThemeList().remove("padding");

        tabs.add((SerializableSupplier<com.vaadin.flow.component.Component>) () -> this.systemEventSearchView, "System Events");
//        tabs.add((SerializableSupplier<com.vaadin.flow.component.Component>) () -> modulesLayout, "Modules");

        this.add(tabs);
        this.setSizeFull();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(this.tabs == null) {
            init();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {

    }
}
