package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.ParameterName;

import java.util.List;

/**
 * Created by Ikasan Development Team on 04/04/2017.
 */
public class NewMappingConfigurationSummaryPanel extends Panel
{

    private MappingConfiguration mappingConfiguration;
    private GridLayout layout = new GridLayout(2, 10);

    public NewMappingConfigurationSummaryPanel(MappingConfiguration mappingConfiguration)
    {
        this.mappingConfiguration = mappingConfiguration;

        init();
    }

    private void init()
    {
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth("100%");

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);


        this.setContent(layout);

        this.setSizeFull();
    }

    public void enter(List<ParameterName> sourceParameterNames, List<ParameterName> targetParameterNames)
    {
        this.layout.removeAllComponents();

        Label mappingConfigurationLabel = new Label("Summary");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        Label clientLabel = new Label("Client");
        clientLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label clientValueLabel = new Label(mappingConfiguration.getConfigurationServiceClient().getName());

        layout.addComponent(clientLabel, 0, 1);
        layout.addComponent(clientValueLabel, 1, 1);

        Label typeLabel = new Label("Type");
        typeLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label typeValueLabel = new Label(mappingConfiguration.getConfigurationType().getName());

        layout.addComponent(typeLabel, 0, 2);
        layout.addComponent(typeValueLabel, 1, 2);

        Label sourceContextLabel = new Label("Source Context");
        sourceContextLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label sourceContextValueLabel = new Label(mappingConfiguration.getSourceContext().getName());

        layout.addComponent(sourceContextLabel, 0, 3);
        layout.addComponent(sourceContextValueLabel, 1, 3);

        Label targetContextLabel = new Label("Target Context");
        targetContextLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label targetContextValueLabel = new Label(mappingConfiguration.getTargetContext().getName());

        layout.addComponent(targetContextLabel, 0, 4);
        layout.addComponent(targetContextValueLabel, 1, 4);

        Label numSourceParamLabel = new Label("Number of Source Parameters");
        numSourceParamLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label numSourceParamValueLabel = new Label(new Integer(mappingConfiguration.getNumberOfParams()).toString());

        layout.addComponent(numSourceParamLabel, 0, 5);
        layout.addComponent(numSourceParamValueLabel, 1, 5);

        Label numTargetParamLabel = new Label("Number of Target Parameters");
        numTargetParamLabel.setStyleName(ValoTheme.LABEL_BOLD);
        Label numTargetParamValueLabel = new Label(new Integer(mappingConfiguration.getNumTargetValues()).toString());

        layout.addComponent(numTargetParamLabel, 0, 6);
        layout.addComponent(numTargetParamValueLabel, 1, 6);

        if(sourceParameterNames != null && sourceParameterNames.size() > 0)
        {
            Label sourceParamNameLabel = new Label("Source Parameter Names");
            sourceParamNameLabel.setStyleName(ValoTheme.LABEL_BOLD);

            StringBuffer sb = new StringBuffer();

            for(ParameterName name: sourceParameterNames)
            {
                sb.append(name.getName()).append("\n");
            }

            TextArea sourceParamNameValueTextArea = new TextArea();
            sourceParamNameValueTextArea.setWidth("80%");
            sourceParamNameValueTextArea.setRows(4);
            sourceParamNameValueTextArea.setValue(sb.toString());
            sourceParamNameValueTextArea.setReadOnly(true);

            layout.addComponent(sourceParamNameLabel, 0, 7);
            layout.addComponent(sourceParamNameValueTextArea, 1, 7);
        }

        if(targetParameterNames != null && targetParameterNames.size() > 0)
        {
            Label targetParamNameLabel = new Label("Target Parameter Names");
            targetParamNameLabel.setStyleName(ValoTheme.LABEL_BOLD);

            StringBuffer sb = new StringBuffer();

            for(ParameterName name: targetParameterNames)
            {
                sb.append(name.getName()).append("\n");
            }

            TextArea targetParamNameValueTextArea = new TextArea();
            targetParamNameValueTextArea.setWidth("80%");
            targetParamNameValueTextArea.setRows(4);
            targetParamNameValueTextArea.setValue(sb.toString());
            targetParamNameValueTextArea.setReadOnly(true);

            layout.addComponent(targetParamNameLabel, 0, 8);
            layout.addComponent(targetParamNameValueTextArea, 1, 8);
        }
    }
}
