package org.ikasan.dashboard.ui.visualisation.view;

import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.componentfactory.Tooltip;
import com.vaadin.componentfactory.TooltipAlignment;
import com.vaadin.componentfactory.TooltipPosition;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.designer.*;
import org.ikasan.designer.event.CanvasItemDoubleClickEvent;
import org.ikasan.designer.event.CanvasItemDoubleClickEventListener;
import org.ikasan.designer.event.CanvasItemRightClickEvent;
import org.ikasan.designer.event.CanvasItemRightClickEventListener;
import org.ikasan.designer.menu.ShapeContextMenu;
import org.ikasan.designer.pallet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Route(value = "designer", layout = IkasanAppLayout.class)
@UIScope
@PageTitle("Ikasan - Designer")
@Component
public class BusinessStreamDesignerView extends VerticalLayout implements BeforeEnterObserver, CanvasItemRightClickEventListener, CanvasItemDoubleClickEventListener
{
    Logger logger = LoggerFactory.getLogger(BusinessStreamDesignerView.class);


    private Registration broadcasterRegistration;

    private boolean initialised = false;

    private Designer businessStreamDesigner;

    /**
     * Constructor
     */
    public BusinessStreamDesignerView()
    {
        this.setMargin(false);
        this.setSpacing(false);

        this.setHeight("100%");
        this.setWidth("100%");
    }

    private void init()
    {
        businessStreamDesigner = new Designer();
        businessStreamDesigner.addCanvasItemRightClickEventListener(this);
        businessStreamDesigner.addCanvasItemDoubleClickEventListener(this);
        businessStreamDesigner.setSizeFull();
        businessStreamDesigner.addItemPallet(new ItemPallet("General", this.createGeneralPalette()));
        businessStreamDesigner.addItemPallet(new ItemPallet("Integrated Systems", this.createIntegratedSystemsPalette()));
        businessStreamDesigner.addItemPallet(new ItemPallet("Boundaries", this.createBoundariesPalette()));

        this.add(businessStreamDesigner);
    }

    private com.vaadin.flow.component.Component createGeneralPalette(){
        DesignerPalletItem flowImage = new DesignerPalletIconItem("frontend/images/flow.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("FLOW"));

            dialog.open();
        }, 100, 100);
        flowImage.setWidth("30px");
        DragSource.create(flowImage);

        Tooltip tooltip = TooltipHelper.getTooltip(flowImage,"This icon represents an Ikasan flow.", TooltipPosition.RIGHT, TooltipAlignment.RIGHT);

        DesignerPalletItem channelImage = new DesignerPalletIconItem("frontend/images/message-channel.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("FLOW"));

            dialog.open();
        }, 100, 100);
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

    private com.vaadin.flow.component.Component createIntegratedSystemsPalette(){
        DesignerPalletItem computerImage = new DesignerPalletIconItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("COMPUTER"));

            dialog.open();
        }, 100, 100);

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

    private com.vaadin.flow.component.Component createBoundariesPalette(){

        DesignerPalletItem computerImage = new DesignerPalletRectangleItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("BOUNDARY"));

            dialog.open();
        }, 100, 100);
        computerImage.setWidth("30px");
        DragSource.create(computerImage);

        DesignerPalletItem triangleImage = new DesignerPalletTriangleItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("TRIANGLE"));

            dialog.open();
        }, 100, 100);
        triangleImage.setWidth("30px");
        DragSource.create(triangleImage);

        DesignerPalletItem ovalImage = new DesignerPalletOvalItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("OVAL"));

            dialog.open();
        }, 100, 100);
        ovalImage.setWidth("30px");
        DragSource.create(ovalImage);

        DesignerPalletItem circleImage = new DesignerPalletCircleItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("CIRCLE"));

            dialog.open();
        }, 100, 100);
        circleImage.setWidth("30px");
        DragSource.create(circleImage);

        DesignerPalletItem labelImage = new DesignerPalletLabelItem("frontend/images/computer.png", () -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Label"));

            dialog.open();
        }, 100, 100);
        labelImage.setWidth("30px");
        DragSource.create(labelImage);


        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1))
            .withTemplateColumns(new Flex(1))
            .withRowAndColumn(computerImage, 1, 1, 1, 1)
            .withRowAndColumn(triangleImage, 1, 2, 1, 2)
            .withRowAndColumn(ovalImage, 1, 3, 1, 3)
            .withRowAndColumn(circleImage, 1, 4, 1, 4)
            .withRowAndColumn(labelImage, 2, 1, 2, 1)
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

    @Override
    public void rightClickEvent(CanvasItemRightClickEvent canvasItemRightClickEvent) {
        ShapeContextMenu shapeContextMenu = new ShapeContextMenu(this.businessStreamDesigner,
            canvasItemRightClickEvent.getClickLocationX(), canvasItemRightClickEvent.getClickLocationY());
        shapeContextMenu.open();
    }

    @Override
    public void doubleClickEvent(CanvasItemDoubleClickEvent canvasItemDoubleClickEvent) {
        Dialog dialog = new Dialog();

        dialog.add(new H1("Double click!"), new Text(canvasItemDoubleClickEvent.getFigure().toString()));
        dialog.open();
    }
}

