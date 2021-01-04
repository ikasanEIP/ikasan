package org.ikasan.dashboard.ui.search.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.AbstractCloseableResizableDialog;

public class IkasanSearchHelpDialog extends AbstractCloseableResizableDialog
{

    public IkasanSearchHelpDialog()
    {
        super.title.setText(getTranslation("help.search-help-header", UI.getCurrent().getLocale()));

        init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);


        ListItem specialCharactersListItem = new ListItem(getTranslation("help.search-solr-special-characters", UI.getCurrent().getLocale()));
        ListItem whiteSpaceListItem = new ListItem(getTranslation("help.search-whitespace", UI.getCurrent().getLocale()));
        ListItem wildcardListItem = new ListItem(getTranslation("help.search-wildcard", UI.getCurrent().getLocale()));
        ListItem logicListItem = new ListItem(getTranslation("help.search-logic", UI.getCurrent().getLocale()));

        UnorderedList searchTermList = new UnorderedList(specialCharactersListItem, whiteSpaceListItem, wildcardListItem, logicListItem);

        verticalLayout
            .add(
                this.buildHelpComponent("frontend/images/search-icon.png", getTranslation("help.search-header", UI.getCurrent().getLocale()), getTranslation("help.search", UI.getCurrent().getLocale())),
                searchTermList,
                this.buildHelpComponent("frontend/images/wiretap-service.png", getTranslation("help.wiretap-header", UI.getCurrent().getLocale()), getTranslation("help.search-wiretap", UI.getCurrent().getLocale())),
                this.buildHelpComponent("frontend/images/replay-service.png", getTranslation("help.replay-header", UI.getCurrent().getLocale()), getTranslation("help.search-replay", UI.getCurrent().getLocale())),
                this.buildHelpComponent("frontend/images/hospital-service.png", getTranslation("help.hospital-header", UI.getCurrent().getLocale()), getTranslation("help.search-hospital", UI.getCurrent().getLocale())),
                this.buildHelpComponent("frontend/images/error-service.png", getTranslation("help.error-header", UI.getCurrent().getLocale()), getTranslation("help.search-error", UI.getCurrent().getLocale())));

        super.content.add(verticalLayout);
        this.setWidth("85%");
        this.setHeight("85%");
    }

    private Component buildHelpComponent(String image, String header, String helpText) {
        Image helpImage = new Image(image, "");
        helpImage.setHeight("40px");

        Div helpTextParagraph = new Div();
        helpTextParagraph.setText(helpText);

        VerticalLayout helpLayout = new VerticalLayout();
        helpLayout.setWidthFull();
        helpLayout.setMargin(false);
        helpLayout.setSpacing(false);
        H4 headerLabel = new H4(header);
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(helpImage, headerLabel);
        headerLayout.setMargin(false);
        headerLayout.setSpacing(true);
        headerLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, helpImage, headerLabel);
        headerLabel.setWidth("50%");
        helpLayout.add(headerLayout);
        helpLayout.add(helpTextParagraph);
        helpLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, headerLayout, helpTextParagraph);

        return helpLayout;
    }
}
