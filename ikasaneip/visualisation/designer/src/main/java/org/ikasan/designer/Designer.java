package org.ikasan.designer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableConsumer;
import org.ikasan.designer.model.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a visjs network diagram. See http://visjs.org/network_examples.html
 */
@SuppressWarnings("serial")
@Tag("div")
@StyleSheet("./org/ikasan/draw2d/designer.css")
public class Designer extends VerticalLayout implements HasSize {

    Logger logger = LoggerFactory.getLogger(Designer.class);

    private final ObjectMapper mapper = new ObjectMapper();



    public Designer() {
        super();
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/jquery.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/jquery-ui.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/draw2d.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/designer-connector-flow.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/mousetrap.min.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/view.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/RotateRectangleFeedbackSelectionPolicy.js");
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/draw2d/RotateHandle.js");

        // Dont transfer empty options.
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        // Dont transfer getter and setter
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withGetterVisibility(Visibility.NONE).withSetterVisibility(Visibility.NONE)
            .withIsGetterVisibility(Visibility.NONE).withFieldVisibility(Visibility.ANY));
        // remains utf8 escaped chars
        mapper.configure(Feature.ESCAPE_NON_ASCII, true);

        this.setId("canvas-wrapper");

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(this);

        getElement().addEventListener("vaadin-context-menu-before-open", e -> {
            contextMenu.setVisible(false);
            populateContextMenu();
        });
    }

    private void initConnector() {
        getUI()
            .orElseThrow(() -> new IllegalStateException(
                "Connector can only be initialized for an attached Designer"))
            .getPage()
            .executeJs("window.Vaadin.Flow.designerConnector.initLazy($0)",
                getElement());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initConnector();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // FIXME does not work this.diagamDestroy();
    }

    public void addIcon(String image, double h, double w) {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.addIcon", image, h, w));
    }

    public void addBoundary(double h, double w) {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.addBoundary", h, w));
    }

    public void populateContextMenu() {

        getElement().callJsFunction("$connector.getSelected").then(String.class, result -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text(result));


            try {
                Container container = mapper.readValue(result, Container.class);
                dialog.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "align-self", "flex-start");
                dialog.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "position", "absolute");
                dialog.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "left", container.getX()+"px");
                dialog.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "top", container.getY()+"px");
                dialog.open();
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    public void bringToFront() {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.bringToFront"));
    }

    public void sendToBack() {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.sendToBack"));
    }

    public void group() {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.group"));
    }

    public void ungroup() {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.ungroup"));
    }

    public void setBackgroundColor(String color) {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.setBackgroundColor", color));
    }

    public void rotateSelected(int angle) {
        runBeforeClientResponse(
            ui -> getElement().callJsFunction("$connector.rotate", angle));
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode()
            .runWhenAttached(ui -> ui.beforeClientResponse(this, context -> command.accept(ui)));
    }
}
