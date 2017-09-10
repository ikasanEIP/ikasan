package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;

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
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);

    }


}