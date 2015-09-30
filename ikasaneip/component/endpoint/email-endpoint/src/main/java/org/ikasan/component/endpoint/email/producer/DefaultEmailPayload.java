package org.ikasan.component.endpoint.email.producer;
/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xualys on 16/09/2015.
 */
public class DefaultEmailPayload implements EmailPayload {

    private Map<String, byte[]> attachmentsContent;
    private Map<String, String> attachmentsType;
    private List<String> attachmentNames;
    private String emailBody;

    /**
     *
     * @param emailBodyFromConfig
     * @param emailFormat
     * @return If email body is not available from payload, email body from configuration is returned.
     * If email body is available from configuration, email body from configuration is returned.
     */
    @Override
    public String formatEmailBody(String emailBodyFromConfig, String emailFormat) {
        return getEmailBody()==null?emailBodyFromConfig:getEmailBody();
    }

    @Override
    public String getEmailBody() {
        return emailBody;
    }


    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;

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

    @Override
    public String toString() {

        return "DefaultEmailPayload{" +
                "attachmentNames=" + attachmentNames +
                ", payloadEmailBody='" + emailBody + '\'' +
                '}';
    }
}
