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
package org.ikasan.console.service;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Console Email Notifier implementation
 * 
 * @author Ikasan Development Team
 */
public class ConsoleEmailNotifier
{
    /** The logger instance */
    private static final Logger logger = Logger.getLogger(ConsoleEmailNotifier.class);

    /** Mail sender */
    private JavaMailSender mailSender;

    /** Mail recipient(s) address */
    private String to;

    /** Mail sender address */
    private String from;

    /** Mail subject */
    private String subject;

    /** Mail body */
    private String body;

    /** Support for multipart message - defaults to false */
    private boolean multipart = false;

    /** Runtime environment (dev, int, uat or prod) */
    private String environment;

    /**
     * Constructor non optional parameters
     * 
     * TODO:  Enforce mandatory fields here
     * 
     * @param mailSender Mail sender
     * @param to email recipient
     * @param from email sender
     * @param environment The runtime environment
     */
    public ConsoleEmailNotifier(final JavaMailSender mailSender, String to, String from, String environment)
    {
        this.mailSender = mailSender;
        this.to = to;
        this.from = from;
        this.environment = environment;
    }

    /**
     * Setter for to address.
     * 
     * @param to email message to address
     */
    public void setTo(String to)
    {
        this.to = to;
    }

    /**
     * Setter for subject
     * 
     * @param subject email message subject
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    
    /**
     * Setter for mail body text.
     * 
     * @param body email message text content
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * Setter for mail
     * 
     * @param from sender email address
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /**
     * Support for multipart message (i.e.: attachments)
     * 
     * TODO: probably this setter is not needed. We will not be sending attachments with email notifications!
     * 
     * @param multipart boolean flag
     */
    public void setMultipart(boolean multipart)
    {
        this.multipart = multipart;
    }

    /**
     * Send the mail
     *   
     * @throws Exception 
     */
    public void sendMail() throws Exception 
    {
        try
        {
            MimeMessage email = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, this.multipart);
            helper.setTo(this.to);
            helper.setFrom(this.from);
            helper.setSubject("[" + this.environment + "] " + this.subject);
            if (this.body != null)
            {
                helper.setText(this.body);
                this.mailSender.send(email);
            }
        }
        catch (Throwable t)
        {
            // TODO improve error handling here
            logger.warn("Failed to send the email." + t.getMessage());
            throw new Exception("Failed to send the email, please contact the System Administrator.", t);
        }
    }
}
