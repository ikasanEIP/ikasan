package org.ikasan.component.endpoint.email.producer;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xualys on 16/09/2015.
 */
public class DefaultEmailPayload implements EmailPayload {

    private String emailBody;
    private String emailFormat;
    private Map<String, byte[]> attachmentsContent;
    private Map<String, String> attachmentsType;
    private List<String> attachmentNames;

    @Override
    public String getEmailBody() {
        return emailBody;
    }

    @Override
    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    @Override
    public String getEmailFormat() {
        return emailFormat==null?"text/plain":emailFormat;
    }

    @Override
    public void setEmailFormat(String emailFormat) {
        this.emailFormat = emailFormat;
    }


    @Override
    public byte[] getAttachment(String name) {
        return attachmentsContent.get(name);
    }

    @Override
    public List<String> getAttachmentNames() {
        return attachmentNames;
    }

    @Override
    public String getAttachmentType(String name) {
        return attachmentsType.get(name);
    }

    public void addAttachment(String name, String type, byte[] content){
        if(attachmentNames == null){
            attachmentNames = new ArrayList<String>();
        }
        attachmentNames.add(name);

        if(attachmentsType == null){
            attachmentsType = new HashMap<String, String>();
        }
        attachmentsType.put(name, type);

        if(attachmentsContent==null){
            attachmentsContent = new HashMap<String, byte[]>();
        }
        attachmentsContent.put(name, content);

    }



}
