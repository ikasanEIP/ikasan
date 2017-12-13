package org.ikasan.component.endpoint.jms.spring.producer;

import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.jms.core.IkasanJmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.transaction.TransactionManager;

/**
 * Created by amajewski on 13/12/2017.
 */
public class ArjunaJmsTemplateProducer extends JmsTemplateProducer
{
    private TransactionManager localTransactionManager;

    /**
     * Constructor
     *
     * @param jmsTemplate
     */
    public ArjunaJmsTemplateProducer(IkasanJmsTemplate jmsTemplate)
    {
        super(jmsTemplate);
    }

    /**
     * If CF not already provided then look it up from JNDI
     */
    protected void establishConnectionFactory()
    {
        super.establishConnectionFactory();

        // proxy an XA CF, but only if its not already been proxied
        if(jmsTemplate.getConnectionFactory() instanceof XAConnectionFactory
            && !(jmsTemplate.getConnectionFactory() instanceof ConnectionFactoryProxy) )
        {
            ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(
                (XAConnectionFactory) jmsTemplate.getConnectionFactory(),
                new TransactionHelperImpl(localTransactionManager)
            );

            jmsTemplate.setConnectionFactory(connectionFactoryProxy);
        }
    }

    public TransactionManager getLocalTransactionManager()
    {
        return localTransactionManager;
    }

    public void setLocalTransactionManager(TransactionManager localTransactionManager)
    {
        this.localTransactionManager = localTransactionManager;
    }
}
