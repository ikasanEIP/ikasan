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
import com.vaadin.flow.component.textfield.TextArea;
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

public abstract class AbstractEntityViewDialog<ENTITY> extends AbstractCloseableResizableDialog
{
    protected DocumentBuilder documentBuilder;
    protected Transformer transformer;
    protected AceEditor aceEditor;
    protected boolean initialised = false;

    protected VerticalLayout content;

    public abstract Component getEntityDetailsLayout();

    public abstract void populate(ENTITY entity);

    public AbstractEntityViewDialog()
    {
        this.setWidth("1px");
        this.setSizeFull();
        setDraggable(true);
        setModal(false);
        setResizable(true);
        setCloseOnEsc(true);

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

    protected void init()
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
}
