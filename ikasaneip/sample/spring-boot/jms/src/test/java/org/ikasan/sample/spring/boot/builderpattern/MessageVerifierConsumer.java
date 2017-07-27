package org.ikasan.sample.spring.boot.builderpattern;

import org.junit.Assert;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageVerifierConsumer
{
    List<String> captureResults= new ArrayList<>();

    @JmsListener(destination = "target", containerFactory = "myJmsContainerFactory" )
    public void receiveMessage(String msg)
    {
        System.out.println("Received :" + msg);
        captureResults.add(msg);

    }

    public List<String> getCaptureResults()
    {
        return captureResults;
    }

}
