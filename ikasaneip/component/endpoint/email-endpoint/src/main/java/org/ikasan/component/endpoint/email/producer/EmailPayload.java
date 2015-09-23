package org.ikasan.component.endpoint.email.producer;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by xualys on 16/09/2015.
 */
public interface EmailPayload{

    public String getEmailBody() ;

    public void setEmailBody(String emailBody);

    public String getEmailFormat();

    public void setEmailFormat(String emailFormat);

    public byte[] getAttachment(String name);

    public List<String> getAttachmentNames();

    public String getAttachmentType(String name);

}
