package org.ikasan.dashboard.ui.general.component;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.material.Material;

@CssImport("./styles/ikasan-dialog.css")
public abstract class AbstractCloseableResizableDialog extends Dialog
{
    protected H2 title = new H2("");

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = true;

    private Header header;
    private Button min;
    private Button max;

    protected VerticalLayout content;

    public AbstractCloseableResizableDialog()
    {
        this.setWidth("1px");
        this.setSizeFull();
        setDraggable(true);
        setModal(false);
        setResizable(true);
        setCloseOnEsc(true);

        // Dialog theming
        getElement().getThemeList().add("ikasan-dialog");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");


        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.getIcon().getElement().getStyle().set("color", "#FFFFFF");
        min.addClickListener(event -> minimise());
        this.min.setVisible(false);

        max = new Button(VaadinIcon.COMPRESS_SQUARE.create());
        max.getIcon().getElement().getStyle().set("color", "#FFFFFF");
        max.addClickListener(event -> maximise());

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.getIcon().getElement().getStyle().set("color", "#FFFFFF");
        close.addClickListener(event -> close());

        title.addClassName("dialog-title");

        header = new Header(title, min, max, close);
        header.getElement().getStyle().set("background-color", "#232F34");
        this.title.getElement().getStyle().set("color", "#FFFFFF");
        add(header);

        content = new VerticalLayout();
        content.setMargin(false);
        content.setSpacing(false);
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

        this.add(content);
        this.setModal(true);
    }

    private void minimise() {
        if (isDocked) {
            initialSize();
        } else {
            if (isFullScreen) {
                initialSize();
            }
            min.setIcon(VaadinIcon.UPLOAD_ALT.create());
            min.getIcon().getElement().getStyle().set("color", "#FFFFFF");
            getElement().getThemeList().add(DOCK);
            getElement().getStyle().set("right", "500px !important");
            setWidth("300px");
        }
        isDocked = !isDocked;
        isFullScreen = false;
        content.setVisible(!isDocked);
    }

    private void initialSize() {
        min.setIcon(VaadinIcon.DOWNLOAD_ALT.create());
        min.getIcon().getElement().getStyle().set("color", "#FFFFFF");
        getElement().getThemeList().remove(DOCK);
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        max.getIcon().getElement().getStyle().set("color", "#FFFFFF");
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void maximise() {
        if (isFullScreen) {
            initialSize();
        } else {
            if (isDocked) {
                initialSize();
            }
            max.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            max.getIcon().getElement().getStyle().set("color", "#FFFFFF");
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }
}
