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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Email Producer Configuration
 *
 * @author Ikasan Development Team
 */
public class EmailProducerConfiguration {

    /** properties to be configured on creation of the mail session */
    protected Map<String,String> extendedMailSessionProperties = new HashMap<String,String>();

    /** The initial debug mode. Default is false. */
    protected boolean mailDebug;

    /** The return email address of the current user, used by the InternetAddress method getLocalAddress. */
    protected String mailFrom;

    /** The MimeMessage class uses the InternetAddress method parseHeader to parse headers in messages. This property controls the strict flag passed to the parseHeader method. The default is true. */
    protected boolean mailMimeAddressStrict = true;

    /** The default host name of the mail server for both Stores and Transports. Used if the mail.protocol.host property isn't set. */
    protected String mailHost = "localhost";

    /** The default host name of the mail server for both Stores and Transports By default the first Store provider in the configuration files is returned. */
    protected String mailStoreProtocol;

    /** The default host name of the mail server for both Stores and Transports By default */
    protected String mailTransportProtocol;

    /** The default user name to use when connecting to the mail server. Used if the mail.protocol.user property isn't set. */
    protected String mailUser;

    /** Specifies the fully qualified class name of the provider for the specified protocol. Used in cases where more than one provider for a given protocol exists; this property can be used to specify which provider to use by default. The provider must still be listed in a configuration file. */
    protected String mailSmtpClass;

    /** The host name of the mail server for the specified protocol. Overrides the mail.host property. */
    protected String mailSmtpHost;

    /** The port number of the mail server for the specified protocol. If not specified the protocol's default port number is used. */
    protected int mailSmtpPort = 25;

    /** The user name to use when connecting to mail servers using the specified protocol. Overrides the mail.user property. */
    protected String mailSmtpUser;

    /** Specifies the fully qualified class name of the provider for the specified protocol. Used in cases where more than one provider for a given protocol exists; this property can be used to specify which provider to use by default. The provider must still be listed in a configuration file. */
    protected String mailPopClass;

    /** The host name of the mail server for the specified protocol. Overrides the mail.host property. */
    protected String mailPopHost;

    /** The port number of the mail server for the specified protocol. If not specified the protocol's default port number is used. */
    protected int mailPopPort = 25;

    /** The user name to use when connecting to mail servers using the specified protocol. Overrides the mail.user property. */
    protected String mailPopUser;

    /** subject line content */
    protected String subject;

    protected String runtimeEnvironment;

    /** email content */
    protected  String emailBody;

    /** email format, e.g. plain text, html */
    protected String emailFormat;

    /** to recipients */
    protected List<String> toRecipients = new ArrayList<String>();

    /** cc recipients */
    protected List<String> ccRecipients = new ArrayList<String>();

    /** bcc recipients */
    protected List<String> bccRecipients = new ArrayList<String>();

    /** flag for attachment */
    protected boolean hasAttachment = false;


    public String getMailSmtpClass() {
        return mailSmtpClass;
    }

    public void setMailSmtpClass(String mailSmtpClass) {
        this.mailSmtpClass = mailSmtpClass;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    public int getMailSmtpPort() {
        return mailSmtpPort;
    }

    public void setMailSmtpPort(int mailSmtpPort) {
        this.mailSmtpPort = mailSmtpPort;
    }

    public String getMailSmtpUser() {
        return mailSmtpUser;
    }

    public void setMailSmtpUser(String mailSmtpUser) {
        this.mailSmtpUser = mailSmtpUser;
    }

    public String getMailPopClass() {
        return mailPopClass;
    }

    public void setMailPopClass(String mailPopClass) {
        this.mailPopClass = mailPopClass;
    }

    public String getMailPopHost() {
        return mailPopHost;
    }

    public void setMailPopHost(String mailPopHost) {
        this.mailPopHost = mailPopHost;
    }

    public int getMailPopPort() {
        return mailPopPort;
    }

    public void setMailPopPort(int mailPopPort) {
        this.mailPopPort = mailPopPort;
    }

    public String getMailPopUser() {
        return mailPopUser;
    }

    public void setMailPopUser(String mailPopUser) {
        this.mailPopUser = mailPopUser;
    }

    public boolean isMailDebug() {
        return mailDebug;
    }

    public boolean getMailDebug() {
        return mailDebug;
    }

    public void setMailDebug(boolean mailDebug) {
        this.mailDebug = mailDebug;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public boolean isMailMimeAddressStrict() {
        return mailMimeAddressStrict;
    }

    public boolean getMailMimeAddressStrict() {
        return mailMimeAddressStrict;
    }

    public void setMailMimeAddressStrict(boolean mailMimeAddressStrict) {
        this.mailMimeAddressStrict = mailMimeAddressStrict;
    }

    public String getMailHost() {
        return mailHost;
    }

    public String getMailStoreProtocol() {
        return mailStoreProtocol;
    }

    public void setMailStoreProtocol(String mailStoreProtocol) {
        this.mailStoreProtocol = mailStoreProtocol;
    }

    public String getMailTransportProtocol() {
        return mailTransportProtocol;
    }

    public void setMailTransportProtocol(String mailTransportProtocol) {
        this.mailTransportProtocol = mailTransportProtocol;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public Map<String, String> getExtendedMailSessionProperties() {
        return extendedMailSessionProperties;
    }

    public void setExtendedMailSessionProperties(Map<String, String> extendedMailSessionProperties) {
        this.extendedMailSessionProperties = extendedMailSessionProperties;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getToRecipients() {
        return toRecipients;
    }

    public void setToRecipients(List<String> toRecipients) {
        this.toRecipients = toRecipients;
    }

    public List<String> getCcRecipients() {
        return ccRecipients;
    }

    public void setCcRecipients(List<String> ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    public List<String> getBccRecipients() {
        return bccRecipients;
    }

    public void setBccRecipients(List<String> bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    /**
     * @return the hasAttachment
     */
    public boolean isHasAttachment()
    {
        return hasAttachment;
    }
    /**
     * @param hasAttachment the hasAttachment to set
     */
    public void setHasAttachment(boolean hasAttachment)
    {
        this.hasAttachment = hasAttachment;
    }

    public String getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    public void setRuntimeEnvironment(String runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public void setEmailBody(String emailBody){ this.emailBody = emailBody; }

    public void setEmailFormat(String emailFormat){this.emailFormat = emailFormat;}

    public String getEmailBody(){return emailBody;}

    public String getEmailFormat(){return emailFormat;}



}
