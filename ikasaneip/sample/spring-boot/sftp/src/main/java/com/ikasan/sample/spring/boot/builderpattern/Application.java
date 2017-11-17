package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.ikasan","com.ikasan"})
public class Application
{

    public static void main(String[] args) throws Exception
    {
        new Application().executeIM(args);
        System.out.println("Context ready");
    }


    public void executeIM(String[] args)
    {
        // get an ikasanApplication instance
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class,args);

    }


}