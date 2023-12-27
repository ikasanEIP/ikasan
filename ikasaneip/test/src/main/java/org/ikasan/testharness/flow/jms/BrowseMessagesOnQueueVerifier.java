package org.ikasan.testharness.flow.jms;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Similar to MessageListenerVerifier but does not create a JMS consumer instead only browses the contents of
 * queues. This can only be used with queues NOT topics.  Should be used when tests are run to share the same spring
 * boot context as MessageListenerVerifier can block consumption by subsequent flows downstream of the destination
 * it was listening on.
 */
public class BrowseMessagesOnQueueVerifier implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(BrowseMessagesOnQueueVerifier.class);

    private Connection connection;

    private String destinationName;

    private List<Object> captureResults = Collections.synchronizedList(new ArrayList<>());

    private Thread browseMessagesOnQueueVerifierThread;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public BrowseMessagesOnQueueVerifier(final String brokerUrl, final String destinationName) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = activeMQConnectionFactory.createConnection();
        this.destinationName = destinationName;
    }

    public void start(){
        browseMessagesOnQueueVerifierThread = new Thread(this);
        browseMessagesOnQueueVerifierThread.start();
    }

    public void stop(){
        running.set(false);
    }

    public List<Object> getCaptureResults() {
        return Collections.unmodifiableList(captureResults);
    }

    @Override
    public void run() {
        try {
            running.set(true);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(destinationName);
            QueueBrowser queueBrowser = session.createBrowser(queue);
            while (running.get()){
                Enumeration msgs = queueBrowser.getEnumeration();
                while (msgs.hasMoreElements()){
                    Object obj = msgs.nextElement();
                    if (!captureResults.contains(obj)){
                        logger.debug("Added new element [{}]", obj);
                        captureResults.add(obj);
                    }
                }
                Thread.sleep(10);
            }
        } catch (JMSException e){
            logger.error("JmsException thrown browsing queue", e);
        } catch (InterruptedException ie){
            logger.error("Interrupted exception browsing queue", ie);
        }
    }

}
