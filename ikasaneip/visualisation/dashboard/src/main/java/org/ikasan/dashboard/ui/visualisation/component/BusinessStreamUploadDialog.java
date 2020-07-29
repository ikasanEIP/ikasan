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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.ikasan.business.stream.metadata.model.BusinessStreamMetaDataImpl;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;

import java.io.IOException;
import java.io.InputStream;

public class BusinessStreamUploadDialog extends Dialog
{
    byte[] businessStreamFile;
    private String businessStreamName;

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
        this.init();
    }

    private void init()
    {
        VerticalLayout verticalLayout = new VerticalLayout();

        Image mrSquidImage = new Image("/frontend/images/mr-squid-head.png", "");
        mrSquidImage.setHeight("35px");

        H3 componentOptions = new H3(String.format(getTranslation("label.upload-business-stream", UI.getCurrent().getLocale())));

        HorizontalLayout header = new HorizontalLayout();
        header.add(mrSquidImage, componentOptions);
        header.setVerticalComponentAlignment(FlexComponent.Alignment.START, mrSquidImage, componentOptions);

        verticalLayout.add(header);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);

        TextField businessStreamNameTextfield = new TextField(getTranslation("label.business-stream-name", UI.getCurrent().getLocale()));
        businessStreamNameTextfield.setWidthFull();

        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload upload = new Upload(fileBuffer);
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

        Button uploadButton = new Button(getTranslation("button.save", UI.getCurrent().getLocale()));
        uploadButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            boolean isValid = true;
            if(businessStreamNameTextfield.getValue() == null || businessStreamNameTextfield.getValue().isEmpty())
            {
                businessStreamNameTextfield.setErrorMessage(getTranslation("error.business-stream-name-empty", UI.getCurrent().getLocale()));
                businessStreamNameTextfield.setInvalid(true);
                isValid = false;
            }

            if(!isValid)
            {
                return;
            }

            if(this.businessStreamFile == null || businessStreamNameTextfield.isEmpty())
            {
                NotificationHelper.showErrorNotification(getTranslation("error.business-stream-file-not-provided", UI.getCurrent().getLocale()));
            }

            this.businessStreamName = businessStreamNameTextfield.getValue();

            BusinessStreamMetaData businessStreamMetaData = new BusinessStreamMetaDataImpl();
            businessStreamMetaData.setId(this.businessStreamName);
            businessStreamMetaData.setName(this.businessStreamName);
            businessStreamMetaData.setJson(new String(this.businessStreamFile));

            this.businessStreamMetaDataService.save(businessStreamMetaData);

            this.close();
        });

        verticalLayout.add(businessStreamNameTextfield, upload, uploadButton);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, upload, uploadButton);
        this.add(verticalLayout);
    }
}
