package org.ikasan.dashboard.ui.general.component;


import com.juicy.JuicyAceEditor;
import com.juicy.mode.JuicyAceMode;
import com.juicy.theme.JuicyAceTheme;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.wontlost.ckeditor.*;
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

public abstract class AbstractEntityViewDialog<ENTITY> extends Dialog
{
    protected DocumentBuilder documentBuilder;
    protected Transformer transformer;
    protected JuicyAceEditor juicyAceEditor;
    protected VaadinCKEditor classicEditor;
    protected AceEditor aceEditor;
    protected boolean initialised = false;
    protected VerticalLayout layout = new VerticalLayout();

    public abstract Component getEntityDetailsLayout();

    public abstract void populate(ENTITY entity);

    public AbstractEntityViewDialog()
    {
        this.setSizeFull();
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
        layout.setWidth("100%");

        layout.add(this.getEntityDetailsLayout(), this.aceEditor);

        this.add(layout);
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
//        juicyAceEditor = new JuicyAceEditor();
//        juicyAceEditor.setTheme(JuicyAceTheme.idle_fingers);
//        juicyAceEditor.setMode(JuicyAceMode.xml);
//        juicyAceEditor.setWidth("100%");
//        juicyAceEditor.setHeight("55vh");
//        juicyAceEditor.setFontsize(12);
//        juicyAceEditor.setSofttabs(false);
//        juicyAceEditor.setTabsize(12);
//        juicyAceEditor.setReadonly(true);
//        juicyAceEditor.setWrapmode(true);

//        /**Classic Editor*/
//        classicEditor = new VaadinCKEditorBuilder().with(builder -> {
//            builder.editorType = EditorType.BALLOON;
//            builder.theme = ThemeType.DARK;
//            builder.toolbar = new Toolbar[]{};
//        }).createVaadinCKEditor();
//        classicEditor.setVisible(true);
//        classicEditor.setWidth("100%");
//        classicEditor.setHeight("550px");

        aceEditor = new AceEditor();

        aceEditor.setTheme(AceTheme.dracula);
        aceEditor.setMode(AceMode.xml);
        aceEditor.setFontSize(11);
        aceEditor.setSoftTabs(false);
        aceEditor.setTabSize(4);
        aceEditor.setWidth("100%");
        aceEditor.setHeight("500px");
        aceEditor.setReadOnly(true);
    }
}
