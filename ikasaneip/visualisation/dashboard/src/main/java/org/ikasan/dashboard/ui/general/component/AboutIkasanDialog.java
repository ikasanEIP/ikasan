package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.util.ApplicationContextProvider;
import org.springframework.boot.info.BuildProperties;

public class AboutIkasanDialog extends AbstractCloseableResizableDialog
{

    public AboutIkasanDialog()
    {
        init();
    }

    private void init()
    {
        BuildProperties buildProperties = (BuildProperties)ApplicationContextProvider.getContext().getBean("buildProperties");

        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 flowOptions = new H3(String.format(getTranslation("label.about", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, flowOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, flowOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, header);

        HorizontalLayout applicationNameLayout = new HorizontalLayout();
        applicationNameLayout.setWidthFull();
        applicationNameLayout.setMargin(false);
        applicationNameLayout.setSpacing(false);
        H4 applicationName = new H4("Application Name");
        applicationName.setWidth("50%");
        Label name = new Label(buildProperties.getName());
        applicationNameLayout.add(applicationName, name);
        applicationNameLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, applicationName, name);

        HorizontalLayout buildVersionLayout = new HorizontalLayout();
        buildVersionLayout.setWidthFull();
        buildVersionLayout.setMargin(false);
        buildVersionLayout.setSpacing(false);
        H4 buildVersion = new H4("Build Version");
        buildVersion.setWidth("50%");
        Label buildVersionLabel = new Label(buildProperties.getVersion());
        buildVersionLayout.add(buildVersion, buildVersionLabel);
        buildVersionLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, buildVersion, buildVersionLabel);

        HorizontalLayout buildTimestampLayout = new HorizontalLayout();
        buildTimestampLayout.setWidthFull();
        buildTimestampLayout.setMargin(false);
        buildTimestampLayout.setSpacing(false);
        H4 buildTimestamp = new H4("Build Timestamp");
        buildTimestamp.setWidth("50%");
        Label timestamp =  new Label("" + buildProperties.getTime().getEpochSecond());
        buildTimestampLayout.add(buildTimestamp, timestamp);
        buildTimestampLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, buildTimestamp, timestamp);

        verticalLayout.add(applicationNameLayout, buildVersionLayout, buildTimestampLayout);

        super.content.add(verticalLayout);
        this.setWidth("600px");
        this.setHeight("500px");
    }
}
