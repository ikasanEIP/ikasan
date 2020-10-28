package org.ikasan.dashboard.ui.visualisation.component;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.ikasan.business.stream.metadata.model.BusinessStreamMetaDataImpl;
import org.ikasan.dashboard.ui.general.component.AbstractCloseableResizableDialog;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;

import java.io.IOException;
import java.io.InputStream;

public class BusinessStreamUploadDialog extends AbstractCloseableResizableDialog
{
    byte[] businessStreamFile;

    private BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService;

    /**
     * Constructor
     *
     * @param businessStreamMetaDataService
     */
    public BusinessStreamUploadDialog(BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService)
    {
        this.businessStreamMetaDataService = businessStreamMetaDataService;
        if(this.businessStreamMetaDataService == null)
        {
            throw new IllegalArgumentException("businessStreamMetaDataService cannot be null!");
        }
        this.init(null);
    }

    /**
     * Constructor
     *
     * @param businessStreamMetaDataService
     */
    public BusinessStreamUploadDialog(BusinessStreamMetaData businessStreamMetaData, BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService)
    {
        this.businessStreamMetaDataService = businessStreamMetaDataService;
        if(this.businessStreamMetaDataService == null)
        {
            throw new IllegalArgumentException("businessStreamMetaDataService cannot be null!");
        }
        this.init(businessStreamMetaData);
    }

    private void init(BusinessStreamMetaData businessStreamMetaData)
    {
        this.setModal(true);

        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        Label businessStreamHeader = new Label(String.format(getTranslation("label.upload-business-stream", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("40px");
        header.add(mrSquidImage, businessStreamHeader);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, mrSquidImage, businessStreamHeader);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        TextField businessStreamNameTextfield = new TextField(getTranslation("label.business-stream-name", UI.getCurrent().getLocale()));
        businessStreamNameTextfield.setWidthFull();
        if(businessStreamMetaData != null) {
            businessStreamNameTextfield.setValue(businessStreamMetaData.getName());
        }

        TextArea businessStreamDescriptionTextfield = new TextArea(getTranslation("label.business-stream-description", UI.getCurrent().getLocale()));
        businessStreamDescriptionTextfield.setWidthFull();
        businessStreamDescriptionTextfield.setHeight("200px");
        if(businessStreamMetaData != null && businessStreamMetaData.getDescription() != null) {
            businessStreamDescriptionTextfield.setValue(businessStreamMetaData.getDescription());
        }

        if(businessStreamMetaData != null && businessStreamMetaData.getJson() != null) {
            this.businessStreamFile = businessStreamMetaData.getJson().getBytes();
        }

        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setMaxFiles(1);
        upload.addFinishedListener(event -> {
            InputStream inputStream =
                fileBuffer.getInputStream();

            try
            {
                businessStreamFile = new byte[inputStream.available()];
                inputStream.read(businessStreamFile);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        Button saveButton = new Button(getTranslation("button.save", UI.getCurrent().getLocale()));
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            boolean isValid = true;
            if(businessStreamNameTextfield.getValue() == null || businessStreamNameTextfield.getValue().isEmpty())
            {
                businessStreamNameTextfield.setErrorMessage(getTranslation("error.business-stream-name-empty", UI.getCurrent().getLocale()));
                businessStreamNameTextfield.setInvalid(true);
                isValid = false;
            }

            if(businessStreamDescriptionTextfield.getValue() == null || businessStreamDescriptionTextfield.getValue().isEmpty())
            {
                businessStreamDescriptionTextfield.setErrorMessage(getTranslation("error.business-stream-description-empty", UI.getCurrent().getLocale()));
                businessStreamDescriptionTextfield.setInvalid(true);
                isValid = false;
            }

            if(this.businessStreamFile == null)
            {
                NotificationHelper.showErrorNotification(getTranslation("error.business-stream-file-not-provided", UI.getCurrent().getLocale()));
                isValid = false;
            }

            if(!isValid)
            {
                return;
            }

            if(businessStreamMetaData != null) {
                this.businessStreamMetaDataService.delete(businessStreamMetaData.getId());
            }

            BusinessStreamMetaData saveBusinessStreamMetaData = new BusinessStreamMetaDataImpl();
            saveBusinessStreamMetaData.setId(businessStreamNameTextfield.getValue());
            saveBusinessStreamMetaData.setName(businessStreamNameTextfield.getValue());
            saveBusinessStreamMetaData.setDescription(businessStreamDescriptionTextfield.getValue());
            saveBusinessStreamMetaData.setJson(new String(this.businessStreamFile));

            this.businessStreamMetaDataService.save(saveBusinessStreamMetaData);

            this.close();
        });

        Button cancelButton = new Button(getTranslation("button.cancel", UI.getCurrent().getLocale()));
        cancelButton.addClickListener(buttonClickEvent -> this.close());

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton, cancelButton);

        verticalLayout.add(businessStreamNameTextfield, businessStreamDescriptionTextfield, upload, buttonLayout);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, upload, buttonLayout);
        this.content.add(verticalLayout);
        super.setWidth("600px");
        super.setHeight("600px");
    }
}
