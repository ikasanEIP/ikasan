package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import org.ikasan.dashboard.ui.search.SearchConstants;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.util.DateFormatter;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrGeneralService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SearchResultsDialog extends Dialog {
    private SolrSearchFilteringGrid searchResultsGrid;
    private Label resultsLabel = new Label();
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;

    public SearchResultsDialog(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService){
        this.solrGeneralService = solrGeneralService;

        this.createSearchResultsGrid();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("1600px");
        layout.setHeight("800px");

        this.resultsLabel.setVisible(false);
        layout.add(this.resultsLabel);
        layout.add(this.searchResultsGrid);

        this.add(layout);

        this.setSizeFull();
    }

    /**
     * Create the grid that the search results appear.
     */
    private void createSearchResultsGrid()
    {
        SearchFilter searchFilter = new SearchFilter();

        this.searchResultsGrid = new SolrSearchFilteringGrid(this.solrGeneralService, searchFilter, this.resultsLabel);

        // Add the icon column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("100%");
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.WIRETAP))
            {
                Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
                wiretapImage.setHeight("30px");
                horizontalLayout.add(wiretapImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.ERROR))
            {
                Image errorImage = new Image("frontend/images/error-service.png", "");
                errorImage.setHeight("30px");
                horizontalLayout.add(errorImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.EXCLUSION))
            {
                Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
                hospitalImage.setHeight("30px");
                horizontalLayout.add(hospitalImage);
            }
            else if(ikasanSolrDocument.getType().equalsIgnoreCase(SearchConstants.REPLAY)) {
                Image replayImage = new Image("frontend/images/replay-service.png", "");
                replayImage.setHeight("30px");
                horizontalLayout.add(replayImage);
            }

            return horizontalLayout;
        })).setWidth("40px");

        // Add the module name column to the grid
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getModuleName)
            .setKey("modulename")
            .setHeader(getTranslation("table-header.module-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(4);

        // Add the flow name column to the grid
        this.searchResultsGrid.addColumn(IkasanSolrDocument::getFlowName).setKey("flowname")
            .setHeader(getTranslation("table-header.flow-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6);

        // Add the component name column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.componentName]]</div>")
            .withProperty("componentName",
                ikasanSolrDocument -> Optional.ofNullable(ikasanSolrDocument.getComponentName()).orElse(getTranslation("label.not-applicable", UI.getCurrent().getLocale()))))
            .setKey("componentname")
            .setHeader(getTranslation("table-header.component-name", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(6);

        // Add the event identifier column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.eventIdentifier]]</div>")
            .withProperty("eventIdentifier",
                ikasanSolrDocument -> ikasanSolrDocument.getEventId()))
            .setKey("eventId")
            .setHeader(getTranslation("table-header.event-id", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(8);

        // Add the event details column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.event]]</div>")
            .withProperty("event",
                ikasanSolrDocument -> {
                    if(ikasanSolrDocument.getType().equals("error"))
                    {
                        if (ikasanSolrDocument.getErrorMessage() == null)
                        {
                            return "";
                        } else
                        {
                            int endIndex = ikasanSolrDocument.getErrorMessage().length() > 200 ? 200 : ikasanSolrDocument.getErrorMessage().length();
                            return ikasanSolrDocument.getErrorMessage().substring(0, endIndex);
                        }
                    }
                    else
                    {
                        if (ikasanSolrDocument.getEvent() == null)
                        {
                            return "";
                        } else
                        {
                            int endIndex = ikasanSolrDocument.getEvent().length() > 200 ? 200 : ikasanSolrDocument.getEvent().length();
                            return ikasanSolrDocument.getEvent().substring(0, endIndex);
                        }
                    }
                }))
            .setKey("event")
            .setHeader(getTranslation("table-header.event-details", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(12);

        // Add the timestamp column to the grid
        this.searchResultsGrid.addColumn(TemplateRenderer.<IkasanSolrDocument>of(
            "<div>[[item.date]]</div>")
            .withProperty("date",
                ikasanSolrDocument -> DateFormatter.getFormattedDate(ikasanSolrDocument.getTimeStamp()))).setHeader(getTranslation("table-header.timestamp", UI.getCurrent().getLocale()))
            .setSortable(true)
            .setFlexGrow(2);

        // Add the select column to the grid
        this.searchResultsGrid.addColumn(new ComponentRenderer<>(ikasanSolrDocument ->
        {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            Checkbox checkbox = new Checkbox();
            checkbox.setId(ikasanSolrDocument.getId());
            horizontalLayout.add(checkbox);

//            checkbox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>) checkboxBooleanComponentValueChangeEvent ->
//            {
//                if(checkboxBooleanComponentValueChangeEvent.getValue())
//                {
//                    this.selectionItems.put(ikasanSolrDocument.getId(), ikasanSolrDocument);
//                }
//                else
//                {
//                    this.selectionItems.remove(ikasanSolrDocument.getId());
//                }
//            });
//
//            if(!this.selectionBoxes.containsKey(ikasanSolrDocument.getId()))
//            {
//                checkbox.setValue(selected);
//                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
//            }
//            else
//            {
//                checkbox.setValue(selectionBoxes.get(ikasanSolrDocument.getId()).getValue());
//                this.selectionItems.put(ikasanSolrDocument.getId(), ikasanSolrDocument);
//                this.selectionBoxes.put(ikasanSolrDocument.getId(), checkbox);
//            }

            return horizontalLayout;
        })).setWidth("20px");

        // Add the double click replayEventSubmissionListener to the grid so that the relevant dialog can be opened.
        this.searchResultsGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<IkasanSolrDocument>>)
            ikasanSolrDocumentItemDoubleClickEvent ->
            {
                if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.WIRETAP))
                {
                    WiretapDialog wiretapDialog = new WiretapDialog();
                    wiretapDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.ERROR))
                {
                    ErrorDialog errorDialog = new ErrorDialog();
                    errorDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.REPLAY))
                {
//                    ReplayDialog replayDialog = new ReplayDialog();
//                    replayDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
                else if(ikasanSolrDocumentItemDoubleClickEvent.getItem().getType().equalsIgnoreCase(SearchConstants.EXCLUSION))
                {
//                    HospitalDialog hospitalDialog = new HospitalDialog();
//                    hospitalDialog.populate(ikasanSolrDocumentItemDoubleClickEvent.getItem());
                }
            });

//        exclusionDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
//        {
//            if (!dialogOpenedChangeEvent.isOpened())
//            {
//                searchResultsGrid.getDataProvider().refreshAll();
//            }
//        });

        // Add filtering to the relevant columns.
        HeaderRow hr = searchResultsGrid.appendHeaderRow();
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setModuleNameFilter, "modulename");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setFlowNameFilter, "flowname");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setComponentNameFilter, "componentname");
        this.searchResultsGrid.addGridFiltering(hr, searchFilter::setEventIdFilter, "eventId");

        this.searchResultsGrid.setSizeFull();
    }

    public void search(long startTime, long endTime, String searchTerm, String type, boolean negateQuery, String moduleName, String flowName) {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setModuleNameFilter(moduleName);
        searchFilter.setFlowNameFilter(flowName);

        this.searchResultsGrid.init(startTime, endTime, searchTerm, Arrays.asList(type), negateQuery, searchFilter);
        this.resultsLabel.setVisible(true);
    }
}
