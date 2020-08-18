package org.ikasan.dashboard.ui.visualisation.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.model.flow.AbstractWiretapNode;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.spec.metadata.DecoratorMetaData;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WiretapManagementDialog extends Dialog
{
    private TriggerRestServiceImpl triggerRestService;
    private List<DecoratorMetaData> decoratorMetaDataList;
    private Module module;
    private Flow flow;
    private Double x;
    private Double y;
    private Integer w;
    private Integer h;
    private NetworkDiagram networkDiagram;

    protected WiretapManagementDialog(TriggerRestServiceImpl triggerRestService
        , Module module, Flow flow, List<DecoratorMetaData> decoratorMetaDataList
        , Double x, Double y, Integer w, Integer h, NetworkDiagram networkDiagram)
    {
        this.triggerRestService = triggerRestService;
        this.module = module;
        this.flow = flow;
        this.decoratorMetaDataList = decoratorMetaDataList;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.networkDiagram = networkDiagram;

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 flowOptions = new H3(String.format(getTranslation("label.wiretap-management", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, flowOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, flowOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        try {
            Div div = new Div();
            div.setText(new ObjectMapper().writeValueAsString(this.decoratorMetaDataList));

            verticalLayout.add(div);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, div);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Button removeWiretapButton = new Button(getTranslation("button.remove-wiretap", UI.getCurrent().getLocale()));
        removeWiretapButton.setWidthFull();
        removeWiretapButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
            buttonClickEvent -> {
                ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog(false);
                progressIndicatorDialog.open("Removing wiretap");

                AtomicBoolean success = new AtomicBoolean(true);

                this.decoratorMetaDataList.forEach(decoratorMetaData -> {
                    if(!this.triggerRestService.delete(this.module.getUrl(), decoratorMetaData.getConfigurationId())) {
                        success.set(false);
                    };
                });

                if(success.get()) {
                    UI.getCurrent().access(() -> this.networkDiagram.removeImage(x, y, h, w));
                    UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
                    NotificationHelper.showUserNotification("Wiretap removed.");
                }
                else {
                    NotificationHelper.showErrorNotification("The has been a problem removing a wiretap. " +
                        "Please contact Ikasan support.");
                }

                progressIndicatorDialog.close();
                this.close();
        });

        ComponentSecurityVisibility.applySecurity(removeWiretapButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_ADMIN
            , SecurityConstants.WIRETAP_WRITE);

        verticalLayout.add(removeWiretapButton);

        this.add(verticalLayout);
    }
}
