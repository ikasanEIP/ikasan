/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.monitor;

import javax.mail.MessagingException;
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

    /** Mail recipient(s) address*/
    private String to;

    /** Mail sender address*/
    private String from;

    /** Mail subject*/
    private String subject;

    /** Mail body*/
    private String body;

    /** Support for multipart message - defaults to false */
    private boolean multipart = false;

    /** Runtime environment (dev, int, uat or prod)*/
    private String environment;

    /**
     * Constructor
     * non optional parameters
     * 
     * @param name Monitor name
     * @param mailSender Mail sender
     * @param from email sender
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
     * Setter for mail message subject.
     * @param subject email subject
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * Setter for mail body text.
     * @param body email message text content
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * Setter for mail 
     * @param from
     */
    public void setFrom(String from)
    {
        this.from = from;
    }
    /**
     * Support for multipart message (i.e.: attachments)
     * 
     * TODO: probably this setter is not needed. We will not be sending attachments 
     * with email notifications!
     * 
     * @param multipart boolean flag
     */
    public void setMultiparty(boolean multipart)
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
            helper.setSubject("[" + this.environment + "]." + this.subject);
            if (state != null)
            {
                if (state.equals("stoppedInError"))
                {
                    helper.setText(this.body);
                    this.mailSender.send(email);
                    logger.info("monitor mail sent tp [" + this.to + "].");
                }
                //otherwise don't bother 
            }
            else
            {
                helper.setText("Status unknown!!");
                this.mailSender.send(email);
                logger.info("monitor mail sent tp [" + this.to + "].");
            }
        }
        catch (MessagingException e)
        {
            //Don't want to disrupt the flow so just log the exception at this point.
            logger.warn("Monitor failed!!!");
        }
    }
}
