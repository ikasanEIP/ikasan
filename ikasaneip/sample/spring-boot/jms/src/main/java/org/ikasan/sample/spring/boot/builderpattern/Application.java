package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration

public class Application
{

    public static void main(String[] args) throws Exception
    {
        Application myApplication = new Application();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);



    }
}