package org.ikasan.dashboard.ui.visualisation.view;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.ironicons.EditorIcons;
import com.flowingcode.vaadin.addons.ironicons.IronIcons;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.componentfactory.Tooltip;
import com.vaadin.componentfactory.TooltipAlignment;
import com.vaadin.componentfactory.TooltipPosition;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.Divider;
import org.ikasan.dashboard.ui.general.component.SearchResults;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamFilteringGrid;
import org.ikasan.dashboard.ui.visualisation.component.ModuleFilteringGrid;
import org.ikasan.designer.Designer;
import org.ikasan.designer.DesignerPaletteImage;
import org.ikasan.designer.DesignerPaletteImageType;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.spec.module.client.MetaDataService;
import org.ikasan.spec.module.client.ModuleControlService;
import org.ikasan.spec.module.client.TriggerService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Route(value = "designer", layout = IkasanAppLayout.class)
@UIScope
@PageTitle("Ikasan - Designer")
@Component
public class BusinessStreamDesignerView extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(BusinessStreamDesignerView.class);

    @Resource
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    @Resource
    private ModuleControlService moduleControlRestService;

    @Autowired
    private ModuleMetaDataService moduleMetadataService;

    @Autowired
    private ConfigurationService configurationRestService;

    @Autowired
    private TriggerService triggerRestService;

    @Resource
    private ConfigurationMetaDataService configurationMetadataService;

    @Resource
    private BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService;

    @Resource
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;

    @Resource
    private HospitalAuditService hospitalAuditService;

    @Resource
    private ResubmissionRestServiceImpl resubmissionRestService;

    @Resource
    private ReplayRestServiceImpl replayRestService;

    @Resource
    private BatchInsert replayAuditService;

    @Resource
    private MetaDataService metaDataApplicationRestService;

    @Resource
    private BatchInsert<ModuleMetaData> moduleMetadataBatchInsert;

    private SearchResults searchResults;


    private ModuleFilteringGrid modulesGrid;
    private BusinessStreamFilteringGrid businessStreamGrid;
    private GraphViewBusinessStreamVisualisation businessStreamVisualisation;
    private GraphViewModuleVisualisation moduleVisualisation;
    private H2 moduleLabel = new H2();

    private Registration broadcasterRegistration;

    private boolean initialised = false;


    /**
     * Constructor
     */
    public BusinessStreamDesignerView()
    {
        this.setMargin(false);
        this.setSpacing(false);
//        getElement().getThemeList().remove("padding");

        this.setHeight("100%");
        this.setWidth("100%");
    }

    private void init()
    {
        Designer designer = new Designer();
        designer.setSizeUndefined();

        DropTarget<Designer> dropTarget = DropTarget.create(designer);

        dropTarget.addDropListener(event -> {
            // move the dragged component to inside the drop target component
            event.getDragSourceComponent().ifPresent(action -> {
                switch(((DesignerPaletteImage)action).getDesignerPaletteImageType()) {
                    case FLOW: case MESSAGE_CHANNEL:
                        designer.addIcon(((DesignerPaletteImage)action).getSrc(), 62, 95);
                        break;
                    case INTEGRATED_SYSTEM:
                        designer.addIcon(((DesignerPaletteImage)action).getSrc(), 62, 62);
                        break;
                    case BOUNDARY:
                        designer.addBoundary(100, 100);
                        break;

                }
            });
        });

        Accordion toolAccordion = new Accordion();
        toolAccordion.getElement().getStyle().set("font-size", "8pt");
        toolAccordion.setWidthFull();
        toolAccordion.add("General", this.createGeneralPalette()).addThemeVariants(DetailsVariant.FILLED);
        toolAccordion.add("Integrated Systems", this.createIntegratedSystemsPalette()).addThemeVariants(DetailsVariant.FILLED);
        toolAccordion.add("Boundaries", this.createBoundariesPalette()).addThemeVariants(DetailsVariant.FILLED);
        toolAccordion.close();

        VerticalLayout toolLayout = new VerticalLayout();
        toolLayout.setSpacing(false);
        toolLayout.setMargin(false);
        toolLayout.setWidthFull();
        toolLayout.add(toolAccordion);
        toolLayout.getElement().getThemeList().remove("padding");

        HorizontalLayout designerLayout = new HorizontalLayout();
        designerLayout.setSizeUndefined();
        designerLayout.getElement().getThemeList().remove("padding");
        Div actions = new Div();
        actions.setId("canvas-actions");
        Button groupButton = new Button();
        groupButton.addClickListener(buttonClickEvent -> {
            designer.group();
        });
        groupButton.getElement().appendChild(FontAwesome.Regular.OBJECT_GROUP.create().getElement());
        actions.add(groupButton);
        Button ungroupButton = new Button();
        ungroupButton.addClickListener(buttonClickEvent -> {
            designer.ungroup();
        });
        ungroupButton.getElement().appendChild(FontAwesome.Regular.OBJECT_UNGROUP.create().getElement());
        actions.add(ungroupButton);
        Button toFrontButton = new Button();
        toFrontButton.addClickListener(buttonClickEvent -> {
            designer.bringToFront();
        });
        toFrontButton.getElement().appendChild(IronIcons.FLIP_TO_FRONT.create().getElement());
        actions.add(toFrontButton);
        Button toBackButton = new Button();
        toBackButton.addClickListener(buttonClickEvent -> {
            designer.sendToBack();
        });
        toBackButton.getElement().appendChild(IronIcons.FLIP_TO_BACK.create().getElement());

        actions.add(toBackButton, getDivider());
        Button undoButton = new Button();
        undoButton.getElement().appendChild(IronIcons.UNDO.create().getElement());
        actions.add(undoButton);
        Button redoButton = new Button();
        redoButton.getElement().appendChild(IronIcons.REDO.create().getElement());
        actions.add(redoButton, getDivider());
        Button zoomInButton = new Button();
        zoomInButton.getElement().appendChild(IronIcons.ZOOM_IN.create().getElement());
        actions.add(zoomInButton);
        Button zoomOutButton = new Button();
        zoomOutButton.getElement().appendChild(IronIcons.ZOOM_OUT.create().getElement());
        actions.add(zoomOutButton, getDivider());
        Button paintButton = new Button();
        paintButton.getElement().appendChild(EditorIcons.FORMAT_COLOR_FILL.create().getElement());
        actions.add(paintButton, getDivider());
        Button copyButton = new Button();
        copyButton.getElement().appendChild(IronIcons.CONTENT_COPY.create().getElement());
        actions.add(copyButton);
        Button pasteButton = new Button();
        pasteButton.getElement().appendChild(IronIcons.CONTENT_PASTE.create().getElement());
        actions.add(pasteButton);

        Div tools = new Div();
        tools.setId("canvas-tools");
        tools.add(toolLayout);

        designerLayout.add(actions, tools, designer);
        this.add(designerLayout);
    }

    private Image getDivider() {
        Image divider = new Image("frontend/images/separator.png", "");
        divider.setWidth("5px");
        divider.setHeight("20px");
        divider.getStyle().set("display", "inline-block");
        divider.getStyle().set("vertical-align", "middle");

        return divider;
    }

    private FluentGridLayout createGeneralPalette(){
        DesignerPaletteImage flowImage = new DesignerPaletteImage(DesignerPaletteImageType.FLOW, "frontend/images/flow.png", "");
        flowImage.setWidth("30px");
        DragSource.create(flowImage);

        Tooltip tooltip = TooltipHelper.getTooltip(flowImage,"This icon represents an Ikasan flow.", TooltipPosition.RIGHT, TooltipAlignment.RIGHT);

        DesignerPaletteImage channelImage = new DesignerPaletteImage(DesignerPaletteImageType.MESSAGE_CHANNEL, "frontend/images/message-channel.png", "");
        channelImage.setWidth("30px");
        DragSource.create(channelImage);

        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(flowImage, 1, 1, 1, 1)
            .withRowAndColumn(flowImage, 1, 1, 1, 1)
            .withRowAndColumn(channelImage, 1, 2, 1, 2)
            .withPadding(false)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);

        layout.add(tooltip);

        return layout;
    }

    private FluentGridLayout createIntegratedSystemsPalette(){
        DesignerPaletteImage computerImage = new DesignerPaletteImage(DesignerPaletteImageType.INTEGRATED_SYSTEM, "frontend/images/computer.png", "");
        computerImage.setWidth("30px");
        DragSource.create(computerImage);


        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(computerImage, 1, 1, 1, 1)
            .withPadding(false)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);

        return layout;
    }

    private FluentGridLayout createBoundariesPalette(){
        DesignerPaletteImage computerImage = new DesignerPaletteImage(DesignerPaletteImageType.BOUNDARY, "frontend/images/computer.png", "");
        computerImage.setWidth("30px");
        DragSource.create(computerImage);


        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(computerImage, 1, 1, 1, 1)
            .withPadding(false)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);

        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if(!initialised)
        {
            this.init();
            initialised = true;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();

        broadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
                logger.debug("Received flow state: " + flowState);
            });
        });

    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }
}

