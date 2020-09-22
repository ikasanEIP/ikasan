package org.ikasan.dashboard.ui.general.component;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.material.Material;
import io.github.ciesielskis.AceEditor;
import io.github.ciesielskis.AceMode;
import io.github.ciesielskis.AceTheme;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

@CssImport("./styles/ikasan-dialog.css")
public abstract class AbstractEntityViewDialog<ENTITY> extends Dialog
{
    protected DocumentBuilder documentBuilder;
    protected Transformer transformer;
    protected AceEditor aceEditor;
    protected boolean initialised = false;
    protected H2 title = new H2("");

    public String DOCK = "dock";
    public String FULLSCREEN = "fullscreen";

    private boolean isDocked = false;
    private boolean isFullScreen = true;

    private Header header;
    private Button min;
    private Button max;

    private VerticalLayout content;

    public abstract Component getEntityDetailsLayout();

    public abstract void populate(ENTITY entity);

    public AbstractEntityViewDialog()
    {
        this.setSizeFull();
        this.setWidth("1024px");
        setDraggable(true);
        setModal(false);
        setResizable(true);
        setCloseOnEsc(true);

        // Dialog theming
        getElement().getThemeList().add("ikasan-dialog");

        // Accessibility
        getElement().setAttribute("aria-labelledby", "dialog-title");


        min = new Button(VaadinIcon.DOWNLOAD_ALT.create());
        min.addClickListener(event -> minimise());
        this.min.setVisible(false);

        max = new Button(VaadinIcon.COMPRESS_SQUARE.create());
        max.addClickListener(event -> maximise());

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        title.addClassName("dialog-title");

        header = new Header(title, min, max, close);
        header.getElement().getThemeList().add(Material.DARK);
        add(header);

        try
        {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        }
        catch (Exception e)
        {
            throw  new IllegalStateException("Could not construct EventViewDialog!", e);
        }

        initialiseEditor();
    }

    private void init()
    {
        content = new VerticalLayout(this.getEntityDetailsLayout(), this.aceEditor);
        content.setMargin(false);
        content.setSpacing(false);
        content.addClassName("dialog-content");
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(content);
    }

    public void open(String event)
    {
        if(!initialised)
        {
            init();
            initialised = true;
        }

        open();

        String xmlString = formatXml(event);
        aceEditor.setValue(xmlString);
    }

    protected String formatXml(String event)
    {
        String xmlString;
        try
        {
            Document doc = this.documentBuilder
                .parse(new InputSource(new StringReader(event)));

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            xmlString = result.getWriter().toString();
        }
        catch (Exception e)
        {
            xmlString = event;
        }

        return xmlString;
    }

    protected void initialiseEditor()
    {
        aceEditor = new AceEditor();

        aceEditor.setTheme(AceTheme.dracula);
        aceEditor.setMode(AceMode.text);
        aceEditor.setFontSize(11);
        aceEditor.setSoftTabs(false);
        aceEditor.setTabSize(4);
        aceEditor.setWidth("auto");
        aceEditor.setHeight("50vh");
        aceEditor.setReadOnly(true);
        aceEditor.setWrap(false);
    }

    private void minimise() {
        if (isDocked) {
            initialSize();
        } else {
            if (isFullScreen) {
                initialSize();
            }
            min.setIcon(VaadinIcon.UPLOAD_ALT.create());
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
        getElement().getThemeList().remove(DOCK);
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
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
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
        isDocked = false;
    }
}
