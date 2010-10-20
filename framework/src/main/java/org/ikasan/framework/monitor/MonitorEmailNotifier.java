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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.InitiatorState;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Monitor listener implementation which sends emails for state changes. By default, this monitor will generate an email
 * if the initiator its monitoring has current state <code>stoppedInError</code>. To override this behavior, configure the
 * monitor with a {@link Map} of required reported states to email distributions list(s) to notify if current state is a match.
 * 
 * @author Ikasan Development Team
 */
public class MonitorEmailNotifier extends AbstractMonitorListener
{
    /** The logger instance */
    private static final Logger logger = Logger.getLogger(MonitorEmailNotifier.class);

    /** Mail sender */
    private JavaMailSender mailSender;

    /** Mail sender address */
    private String from;

    /** Mail body */
    private String body;

    /** Runtime environment (dev, int, uat or prod) */
    private String environment;

    /** A map of state to email distribution lists that must be notified if current state matches the key */
    private Map<String, List<String>> stateToDistributionListMap;

    /**
     * Create a monitor that notifies a single recipient if initaitor state is <code>stoppedInError</code>.
     * @param name Monitor name
     * @param mailSender Mail sender
     * @param to email distribution list
     * @param environment Runtime environment
     */
    public MonitorEmailNotifier(final String name, final JavaMailSender mailSender, final String to, final String environment)
    {
        super(name);
        this.environment = environment;
        this.mailSender = mailSender;
        List<String> toList = new ArrayList<String>();
        toList.add(to);
        this.stateToDistributionListMap = new HashMap<String, List<String>>();
        this.stateToDistributionListMap.put(InitiatorState.ERROR.getName(), toList);
    }

    /**
     * Creates a monitor that notifies configured recipients if initiator state is <code>stoppedInError</code> only.
     * 
     * @param name Monitor name
     * @param mailSender Mail sender
     * @param to email distribution list
     * @param environment Runtime environment
     */
    public MonitorEmailNotifier(final String name, final JavaMailSender mailSender, final List<String> to, final String environment)
    {
        super(name);
        this.environment = environment;
        this.mailSender = mailSender;
        this.stateToDistributionListMap = new HashMap<String, List<String>>();
        this.stateToDistributionListMap.put(InitiatorState.ERROR.getName(), to);
    }

    /**
     * Creates a monitor that matches current initiator state to a map of pre-configured states to recipient list.
     * 
     * @param name Monitor name
     * @param mailSender Mail sender
     * @param stateToDistributionListMap map of initiator states to recipient list
     * @param environment Runtime envirnoment
     */
    public MonitorEmailNotifier(final String name, final JavaMailSender mailSender, final Map<String, List<String>> stateToDistributionListMap, final String environment)
    {
        super(name);
        this.environment = environment;
        this.mailSender = mailSender;
        this.stateToDistributionListMap = stateToDistributionListMap;
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
     * Setter for mail from field
     * 
     * @param from sender email address
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.monitor.AbstractMonitorListener#notify(java.lang.String)
     */
    @Override
    public void notify(final String state)
    {
        try
        {
            if (this.stateToDistributionListMap.containsKey(state))
            {
                // Get the recipient(s) distribution list
                List<String> recipientList = this.stateToDistributionListMap.get(state);

                // Create the email
                MimeMessage email = this.mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(email);
                if (this.from != null)
                {
                    // Set 'from'
                    helper.setFrom(this.from);
                }

                // Set 'to'
                helper.setTo(recipientList.toArray(new String [recipientList.size()]));

                // Set 'subject'
                helper.setSubject("[" + this.environment + "] Initiator Email Notifier");

                // Set 'body'
                String text = "Initiator [" + this.getName() + "] is reporting status [" + state + "].";
                if (this.body != null)
                {
                    text = text + this.body;
                }
                helper.setText(text);

                // Send away
                this.mailSender.send(email);
            }
            else
            {
                logger.warn("Initiator reporting unsupported state [" + state + "]. No email will be sent.");
            }
        }
        catch (Throwable t)
        {
            // Don't want to disrupt the flow so just log the exception at this point.
            logger.warn("Monitor failed to create email notification.");
        }
    }
}
