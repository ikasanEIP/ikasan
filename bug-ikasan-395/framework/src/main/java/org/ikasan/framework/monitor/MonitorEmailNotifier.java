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
package org.ikasan.framework.monitor;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Monitor listener implementation which notifies the JNDI of state changes.
 * 
 * @author Ikasan Development Team
 */
public class MonitorEmailNotifier extends AbstractMonitorListener
{
    /** The logger instance */
    private static final Logger logger = Logger.getLogger(MonitorEmailNotifier.class);

    /** Mail sender */
    private JavaMailSender mailSender;

    /** Mail recipient(s) address */
    private String to;

    /** Mail sender address */
    private String from;

    /** Mail body */
    private String body;

    /** Support for multipart message - defaults to false */
    private boolean multipart = false;

    /** Runtime environment (dev, int, uat or prod) */
    private String environment;

    /**
     * Constructor non optional parameters
     * 
     * @param name Monitor name
     * @param mailSender Mail sender
     * @param to email recipient
     * @param environment The runtime environment
     */
    public MonitorEmailNotifier(String name, final JavaMailSender mailSender, String to, String environment)
    {
        super(name);
        this.mailSender = mailSender;
        this.to = to;
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

    @Override
    public void notify(String state)
    {
        try
        {
            MimeMessage email = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, this.multipart);
            if (this.from != null)
            {
                helper.setFrom(this.from);
            }
            helper.setTo(this.to);
            helper.setSubject("[" + this.environment + "] Initiator Email Notifier");
            if (state != null)
            {
                if (state.equals("stoppedInError"))
                {
                    if (this.body != null)
                    {
                        helper.setText(this.body);
                    }
                    else
                    {
                        helper.setText(this.getName() + " is reporting status [" + state
                                + "] and requires manual intervention.");
                    }
                    this.mailSender.send(email);
                }
                // otherwise don't bother
            }
            else
            {
                helper.setText(this.getName() + " reporting unknown status. There might be a problem.");
                this.mailSender.send(email);
            }
        }
        catch (Throwable t)
        {
            // Don't want to disrupt the flow so just log the exception at this point.
            logger.warn("Monitor failed to create email notification.");
        }
    }
}
