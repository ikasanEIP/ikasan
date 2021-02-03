package org.ikasan.designer;

import com.flowingcode.vaadin.addons.ironicons.EditorIcons;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

@StyleSheet("./org/ikasan/color-picker/spectrum.css")
public class ColorPicker extends TextField {

    public ColorPicker() {
        init();
    }


    private void init(){
        UI.getCurrent().getPage().addJavaScript("./org/ikasan/color-picker/spectrum.js");
        this.setId("color-picker");

        EditorIcons.Icon icon = EditorIcons.FORMAT_COLOR_FILL.create();
        icon.setSize("18px");
        icon.setColor("rgba(241, 90, 35, 1.0)");

        this.setPrefixComponent(icon);
        this.setMaxLength(0);
        this.setWidth("18px");

        this.setValueChangeMode(ValueChangeMode.TIMEOUT);

        getElement().executeJs("$('#color-picker').spectrum({\n" +
                "  togglePaletteOnly: \"true\",\n" +
                "    showPalette: \"true\",\n" +
                "    showAlpha: true,\n" +
                "    preferredFormat: \"rgb\",\n" +
                "    showInput: true,  \n" +
                "    showButtons: false,  \n" +
                "});");
    }


}
