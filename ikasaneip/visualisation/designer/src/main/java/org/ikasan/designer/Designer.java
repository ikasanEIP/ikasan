package org.ikasan.designer;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.ironicons.IronIcons;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.ikasan.designer.event.CanvasItemDoubleClickEventListener;
import org.ikasan.designer.event.CanvasItemRightClickEventListener;
import org.ikasan.designer.pallet.DesignerPalletItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Designer extends VerticalLayout implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(Designer.class);

    private DesignerCanvas designerCanvas;
    private Accordion toolAccordion = new Accordion();
    private List<ItemPallet> itemPalettes;


    private boolean initialised = false;

    /**
     * Constructor
     */
    public Designer()
    {
        this.setMargin(false);
        this.setSpacing(false);

        this.setHeight("100%");
        this.setWidth("100%");


        this.itemPalettes = new ArrayList<>();
        init();
    }

    private void init()
    {
        this.designerCanvas = new DesignerCanvas();
        this.designerCanvas.setSizeUndefined();

        DropTarget<DesignerCanvas> dropTarget = DropTarget.create(this.designerCanvas);

        dropTarget.addDropListener(event -> {
            // move the dragged component to inside the drop target component
            event.getDragSourceComponent().ifPresent(action -> {
                ((DesignerPalletItem)action).executeCanvasAddAction();
                designerCanvas.addPalletItem(((DesignerPalletItem)action));
                switch(((DesignerPalletItem)action).getDesignerPalletItemType()) {
                    case ICON:
                        designerCanvas.addIcon(((DesignerPalletItem)action).getIdentifier(), ((DesignerPalletItem)action).getSrc(), 62, 62);
                        break;
                    case RECTANGLE:
                        designerCanvas.addBoundary(100, 300);
                        break;
                    case TRIANGLE:
                        designerCanvas.addTriangleBoundary(300, 300);
                        break;
                    case OVAL:
                        designerCanvas.addOval(300, 300);
                        break;
                    case CIRCLE:
                        designerCanvas.addCircle();
                        break;
                    case LABEL:
                        designerCanvas.addLabel();
                        break;
                }
            });
        });

        this.toolAccordion = new Accordion();
        this.toolAccordion.getElement().getStyle().set("font-size", "8pt");
        this.toolAccordion.setWidthFull();
        this.toolAccordion.close();

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
            this.designerCanvas.group();
        });
        groupButton.getElement().appendChild(FontAwesome.Regular.OBJECT_GROUP.create().getElement());
        actions.add(groupButton);
        Button ungroupButton = new Button();
        ungroupButton.addClickListener(buttonClickEvent -> {
            this.designerCanvas.ungroup();
        });
        ungroupButton.getElement().appendChild(FontAwesome.Regular.OBJECT_UNGROUP.create().getElement());
        actions.add(ungroupButton);
        Button toFrontButton = new Button();
        toFrontButton.addClickListener(buttonClickEvent -> {
            this.designerCanvas.bringToFront();
        });
        toFrontButton.getElement().appendChild(IronIcons.FLIP_TO_FRONT.create().getElement());
        actions.add(toFrontButton);
        Button toBackButton = new Button();
        toBackButton.addClickListener(buttonClickEvent -> {
            this.designerCanvas.sendToBack();
        });
        toBackButton.getElement().appendChild(IronIcons.FLIP_TO_BACK.create().getElement());

        actions.add(toBackButton, getDivider());

//        TextField rotateAngle = new TextField("Angle");
//        actions.add(rotateAngle);
        Button undoButton = new Button();
        undoButton.getElement().appendChild(IronIcons.UNDO.create().getElement());
        undoButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
//            designer.rotateSelected(-Integer.valueOf(rotateAngle.getValue()));
        });
        actions.add(undoButton);
        Button redoButton = new Button();
        redoButton.getElement().appendChild(IronIcons.REDO.create().getElement());
        redoButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
//            designer.rotateSelected(Integer.valueOf(rotateAngle.getValue()));
        });
        actions.add(redoButton, getDivider());
        Button zoomInButton = new Button();
        zoomInButton.getElement().appendChild(IronIcons.ZOOM_IN.create().getElement());
        zoomInButton.setId("canvas_zoom_in");
        actions.add(zoomInButton);
        Button zoomOutButton = new Button();
        zoomOutButton.getElement().appendChild(IronIcons.ZOOM_OUT.create().getElement());
        zoomOutButton.setId("canvas_zoom_out");
        actions.add(zoomOutButton, getDivider());

        ColorPicker paintButton = new ColorPicker();
        paintButton.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>)
            textFieldStringComponentValueChangeEvent -> {
                this.designerCanvas.setBackgroundColor(textFieldStringComponentValueChangeEvent.getValue());
        });

//        paintButton.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>)
//            textFieldStringComponentValueChangeEvent -> {
//            icon.getStyle().set("background-color", textFieldStringComponentValueChangeEvent.getValue());
//        });
//        paintButton.addInputListener((ComponentEventListener<InputEvent>) inputEvent -> {
//            icon.getStyle().set("background-color", paintButton.getValue());
//        });
//        paintButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
//           ColorPicker colorPicker = new ColorPicker();
//
//        });


        actions.add(paintButton, getDivider());
        Button copyButton = new Button();
        copyButton.getElement().appendChild(IronIcons.CONTENT_COPY.create().getElement());
        actions.add(copyButton);
        Button pasteButton = new Button();
        pasteButton.getElement().appendChild(IronIcons.CONTENT_PASTE.create().getElement());
        actions.add(pasteButton);

        Checkbox readOnly = new Checkbox("Read Only");
        readOnly.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>)
            checkboxBooleanComponentValueChangeEvent -> this.designerCanvas.setReadonly(checkboxBooleanComponentValueChangeEvent.getValue()));

        actions.add(readOnly);

        Div tools = new Div();
        tools.setId("canvas-tools");
        tools.add(toolLayout);

        designerLayout.add(actions, tools, this.designerCanvas);
        this.add(designerLayout);

        this.initialised = true;
    }

    private Image getDivider() {
        Image divider = new Image("frontend/images/separator.png", "");
        divider.setWidth("5px");
        divider.setHeight("20px");
        divider.getStyle().set("display", "inline-block");
        divider.getStyle().set("vertical-align", "middle");

        return divider;
    }

    public void addItemPallet(ItemPallet itemPallet) {
        this.itemPalettes.add(itemPallet);
        this.toolAccordion.add(itemPallet.getSummary(), itemPallet.getPallet());
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

//        broadcasterRegistration = FlowStateBroadcaster.register(flowState ->
//        {
//            ui.access(() ->
//            {
//                // do something interesting here.
//                logger.debug("Received flow state: " + flowState);
//            });
//        });

    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
//        broadcasterRegistration.remove();
//        broadcasterRegistration = null;
    }

    public void setLineType(String pattern) {
        designerCanvas.setLineType(pattern);
    }

    public void setRadius(double radius) {
        designerCanvas.setRadius(radius);
    }

    public void setStroke(int width) {
        this.designerCanvas.setStroke(width);
    }

    public void setReadonly(boolean readonly) {
        this.designerCanvas.setReadonly(readonly);
    }

    public void addCanvasItemRightClickEventListener(CanvasItemRightClickEventListener listener) {
        this.designerCanvas.addCanvasItemRightClickEventListener(listener);
    }

    public void addCanvasItemDoubleClickEventListener(CanvasItemDoubleClickEventListener listener) {
        this.designerCanvas.addCanvasItemDoubleClickEventListener(listener);
    }

    public void exportJson(){
        this.designerCanvas.exportJson();
    }
}

