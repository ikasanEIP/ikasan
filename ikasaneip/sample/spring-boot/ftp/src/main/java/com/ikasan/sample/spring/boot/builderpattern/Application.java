package com.ikasan.sample.spring.boot.builderpattern;

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
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class,args);

        System.out.println("Context ready");
    }
}