package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.ikasan","com.ikasan"})
public class Application
{

    public static void main(String[] args) throws Exception
    {
        // Embedded broker is started as part of sample
        new EmbeddedActiveMQBroker().start();

        IkasanApplicationFactory.getIkasanApplication(Application.class,args);

        System.out.println("Context ready");
    }
}