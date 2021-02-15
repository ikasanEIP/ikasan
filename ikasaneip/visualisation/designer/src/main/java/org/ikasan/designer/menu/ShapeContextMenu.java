package org.ikasan.designer.menu;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.ikasan.designer.Designer;


public class ShapeContextMenu extends Dialog {

    public ShapeContextMenu(Designer designer, int x, int y) {
        this.setWidth("100px");
        this.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "align-self", "flex-start");
        this.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "position", "absolute");
        this.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "left", x + "px");
        this.getElement().executeJs("this.$.overlay.$.overlay.style[$0]=$1", "top", y + "px");

        Select<Image> select = new Select<>();
        select.setWidth("90%");
        select.setItems(new Image("frontend/images/separator.png", ""));
        select.setItems(new Image("frontend/images/separator.png", ""));
        select.setRenderer(new ComponentRenderer<>(image -> {
            FlexLayout wrapper = new FlexLayout();
            image.setWidth("5px");
            wrapper.add(image);
            return wrapper;
        }));

        select.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<Image>, Image>>)
            selectImageComponentValueChangeEvent -> designer.setLineType(""));

        NumberField numberField = new NumberField("Corner Radius");
        numberField.setHasControls(true);
        numberField.setValue(0d);

        numberField.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>)
            numberFieldDoubleComponentValueChangeEvent -> designer.setRadius(numberFieldDoubleComponentValueChangeEvent.getValue()));

        NumberField strokeField = new NumberField("Line Width");
        strokeField.setHasControls(true);
        strokeField.setValue(0d);

        strokeField.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>)
            numberFieldDoubleComponentValueChangeEvent -> designer.setStroke(numberFieldDoubleComponentValueChangeEvent.getValue().intValue()));

        Button button = new Button("Export");
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            designer.exportJson();
        });

        this.add(select, numberField, strokeField, button);

    }
}
