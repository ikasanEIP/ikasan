package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.model.flow.AbstractWiretapNode;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.spec.metadata.DecoratorMetaData;
import org.ikasan.spec.module.client.TriggerService;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WiretapManagementDialog extends Dialog
{
    public static final String BEFORE = TriggerRelationship.BEFORE.getDescription();
    public static final String AFTER = TriggerRelationship.AFTER.getDescription();
    public static final String WIRETAP = "wiretap";
    public static final String LOG = "log";

    private TriggerService triggerRestService;
    private List<DecoratorMetaData> decoratorMetaDataList;
    private Module module;
    private Flow flow;
    private NetworkDiagram networkDiagram;
    private AbstractWiretapNode abstractWiretapNode;
    private String type;
    private String relationship;

    protected WiretapManagementDialog(TriggerService triggerRestService
        , Module module, Flow flow, List<DecoratorMetaData> decoratorMetaDataList
        , AbstractWiretapNode abstractWiretapNode, NetworkDiagram networkDiagram
        , String type, String relationship)
    {
        this.triggerRestService = triggerRestService;
        this.module = module;
        this.flow = flow;
        this.decoratorMetaDataList = decoratorMetaDataList;
        this.abstractWiretapNode = abstractWiretapNode;
        this.networkDiagram = networkDiagram;
        this.type = type;
        this.relationship = relationship;

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

        Button removeWiretapButton = new Button(getTranslation("button.remove-wiretap", UI.getCurrent().getLocale()));
        removeWiretapButton.setWidthFull();
        removeWiretapButton.addClickListener((ComponentEventListener<ClickEvent<Button>>)
            buttonClickEvent -> {
                AtomicBoolean success = new AtomicBoolean(true);

                this.decoratorMetaDataList.forEach(decoratorMetaData -> {
                    if(!this.triggerRestService.delete(this.module.getUrl(), decoratorMetaData.getConfigurationId())) {
                        success.set(false);
                    };
                });

                if(success.get()) {
                    if(type.equals(WIRETAP)) {
                        if(relationship.equals(BEFORE)){
                            UI.getCurrent().access(() -> this.networkDiagram.removeImage(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getWiretapBeforeImageX(),
                                this.abstractWiretapNode.getY() + this.abstractWiretapNode.getWiretapBeforeImageY(), this.abstractWiretapNode.getWiretapBeforeImageW(),
                                this.abstractWiretapNode.getWiretapBeforeImageW()));
                            UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
                            this.abstractWiretapNode.setWiretapBeforeStatus(NodeFoundStatus.NOT_FOUND);
                        }
                        else if(relationship.equals(AFTER)){
                            UI.getCurrent().access(() -> this.networkDiagram.removeImage(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getWiretapAfterImageX(),
                                this.abstractWiretapNode.getY() + this.abstractWiretapNode.getWiretapAfterImageY(), this.abstractWiretapNode.getWiretapAfterImageW(),
                                this.abstractWiretapNode.getWiretapAfterImageW()));
                            UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
                            this.abstractWiretapNode.setWiretapAfterStatus(NodeFoundStatus.NOT_FOUND);
                        }
                    }
                    else if(type.equals(LOG)) {
                        if(relationship.equals(BEFORE)){
                            UI.getCurrent().access(() -> this.networkDiagram.removeImage(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getLogWiretapBeforeImageX(),
                                this.abstractWiretapNode.getY() + this.abstractWiretapNode.getLogWiretapBeforeImageY(), this.abstractWiretapNode.getLogWiretapBeforeImageW(),
                                this.abstractWiretapNode.getLogWiretapBeforeImageW()));
                            UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
                            this.abstractWiretapNode.setLogWiretapBeforeStatus(NodeFoundStatus.NOT_FOUND);
                        }
                        else if(relationship.equals(AFTER)){
                            UI.getCurrent().access(() -> this.networkDiagram.removeImage(this.abstractWiretapNode.getX() + this.abstractWiretapNode.getLogWiretapAfterImageX(),
                                this.abstractWiretapNode.getY() + this.abstractWiretapNode.getLogWiretapAfterImageY(), this.abstractWiretapNode.getLogWiretapAfterImageW(),
                                this.abstractWiretapNode.getLogWiretapAfterImageW()));
                            UI.getCurrent().access(() -> this.networkDiagram.diagamRedraw());
                            this.abstractWiretapNode.setLogWiretapAfterStatus(NodeFoundStatus.NOT_FOUND);
                        }
                    }

                    NotificationHelper.showUserNotification(getTranslation("notification.wiretap-removed", UI.getCurrent().getLocale()));
                }
                else {
                    NotificationHelper.showErrorNotification(getTranslation("notification.error-removing-wiretap", UI.getCurrent().getLocale()));
                }

                this.close();
        });

        ComponentSecurityVisibility.applySecurity(removeWiretapButton, SecurityConstants.ALL_AUTHORITY
            , SecurityConstants.WIRETAP_ADMIN
            , SecurityConstants.WIRETAP_WRITE);

        verticalLayout.add(removeWiretapButton);

        this.add(verticalLayout);
    }
}
