package org.ikasan.dashboard.ui.component;


import com.juicy.JuicyAceEditor;
import com.juicy.mode.JuicyAceMode;
import com.juicy.theme.JuicyAceTheme;
import com.vaadin.flow.component.dialog.Dialog;
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

public class EventViewDialog extends Dialog
{
    private DocumentBuilder documentBuilder;
    private Transformer transformer;
    private JuicyAceEditor juicyAceEditor;

    public EventViewDialog()
    {
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


        juicyAceEditor = new JuicyAceEditor();
        juicyAceEditor.setTheme(JuicyAceTheme.idle_fingers);
        juicyAceEditor.setMode(JuicyAceMode.xml);
        juicyAceEditor.setWidth("1400px");
        juicyAceEditor.setHeight("1000px");
        juicyAceEditor.setFontsize(12);
        juicyAceEditor.setSofttabs(false);
        juicyAceEditor.setTabsize(12);
        juicyAceEditor.setReadonly(true);
        juicyAceEditor.setWrapmode(true);

        add(juicyAceEditor);
        setWidth("80%");
        setHeight("80%");
    }

    public void open(String event)
    {
        String xmlString = null;
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

        juicyAceEditor.setValue(xmlString);

        open();
    }
}
