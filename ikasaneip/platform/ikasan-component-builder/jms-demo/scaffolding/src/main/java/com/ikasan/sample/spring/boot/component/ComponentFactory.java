package com.ikasan.sample.spring.boot.component;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.component.endpoint.Broker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class ComponentFactory {

@Resource
@Qualifier("jMSProducer")
private Producer jMSProducer;
@Resource
@Qualifier("exceptionGeneratingBroker")
private Broker exceptionGeneratingBroker;
@Resource
@Qualifier("myVerySpecialTranslator")
private Translator myVerySpecialTranslator;
@Resource
@Qualifier("jMSConsumer")
private Consumer jMSConsumer;
@Resource
@Qualifier("myRecipientFlowJMSProducer1")
private Producer myRecipientFlowJMSProducer1;
@Resource
@Qualifier("myRecipientFlowFilter")
private Filter myRecipientFlowFilter;
@Resource
@Qualifier("myRecipientFlowJMSProducer4")
private Producer myRecipientFlowJMSProducer4;
@Resource
@Qualifier("myRecipientFlowJMSProducer5")
private Producer myRecipientFlowJMSProducer5;
@Resource
@Qualifier("mySingleRecipientRouter")
private SingleRecipientRouter mySingleRecipientRouter;
@Resource
@Qualifier("myRecipientFlowJMSProducer3")
private Producer myRecipientFlowJMSProducer3;
@Resource
@Qualifier("myRecipientFlowRouter")
private MultiRecipientRouter myRecipientFlowRouter;
@Resource
@Qualifier("myRecipientFlowConverter")
private Converter myRecipientFlowConverter;
@Resource
@Qualifier("myRecipientFlowJMSConsumer")
private Consumer myRecipientFlowJMSConsumer;
    /**
    * This method returns the Producer associated with the jMSProducer bean.
    *
    * @return The jMSProducer Producer bean.
    */
    public Producer getJMSProducer() {
        return this.jMSProducer;
    }

    /**
    * This method returns the Broker associated with the exceptionGeneratingBroker bean.
    *
    * @return The exceptionGeneratingBroker Broker bean.
    */
    public Broker getExceptionGeneratingBroker() {
        return this.exceptionGeneratingBroker;
    }

    /**
    * This method returns the Translator associated with the myVerySpecialTranslator bean.
    *
    * @return The myVerySpecialTranslator Translator bean.
    */
    public Translator getMyVerySpecialTranslator() {
        return this.myVerySpecialTranslator;
    }

    /**
    * This method returns the Consumer associated with the jMSConsumer bean.
    *
    * @return The jMSConsumer Consumer bean.
    */
    public Consumer getJMSConsumer() {
        return this.jMSConsumer;
    }

    /**
    * This method returns the Producer associated with the myRecipientFlowJMSProducer1 bean.
    *
    * @return The myRecipientFlowJMSProducer1 Producer bean.
    */
    public Producer getMyRecipientFlowJMSProducer1() {
        return this.myRecipientFlowJMSProducer1;
    }

    /**
    * This method returns the Filter associated with the myRecipientFlowFilter bean.
    *
    * @return The myRecipientFlowFilter Filter bean.
    */
    public Filter getMyRecipientFlowFilter() {
        return this.myRecipientFlowFilter;
    }

    /**
    * This method returns the Producer associated with the myRecipientFlowJMSProducer4 bean.
    *
    * @return The myRecipientFlowJMSProducer4 Producer bean.
    */
    public Producer getMyRecipientFlowJMSProducer4() {
        return this.myRecipientFlowJMSProducer4;
    }

    /**
    * This method returns the Producer associated with the myRecipientFlowJMSProducer5 bean.
    *
    * @return The myRecipientFlowJMSProducer5 Producer bean.
    */
    public Producer getMyRecipientFlowJMSProducer5() {
        return this.myRecipientFlowJMSProducer5;
    }

    /**
    * This method returns the SingleRecipientRouter associated with the mySingleRecipientRouter bean.
    *
    * @return The mySingleRecipientRouter SingleRecipientRouter bean.
    */
    public SingleRecipientRouter getMySingleRecipientRouter() {
        return this.mySingleRecipientRouter;
    }

    /**
    * This method returns the Producer associated with the myRecipientFlowJMSProducer3 bean.
    *
    * @return The myRecipientFlowJMSProducer3 Producer bean.
    */
    public Producer getMyRecipientFlowJMSProducer3() {
        return this.myRecipientFlowJMSProducer3;
    }

    /**
    * This method returns the MultiRecipientRouter associated with the myRecipientFlowRouter bean.
    *
    * @return The myRecipientFlowRouter MultiRecipientRouter bean.
    */
    public MultiRecipientRouter getMyRecipientFlowRouter() {
        return this.myRecipientFlowRouter;
    }

    /**
    * This method returns the Converter associated with the myRecipientFlowConverter bean.
    *
    * @return The myRecipientFlowConverter Converter bean.
    */
    public Converter getMyRecipientFlowConverter() {
        return this.myRecipientFlowConverter;
    }

    /**
    * This method returns the Consumer associated with the myRecipientFlowJMSConsumer bean.
    *
    * @return The myRecipientFlowJMSConsumer Consumer bean.
    */
    public Consumer getMyRecipientFlowJMSConsumer() {
        return this.myRecipientFlowJMSConsumer;
    }

}
