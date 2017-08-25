package org.ikasan.sample.spring.boot.builderpattern;

import com.arjuna.ats.arjuna.coordinator.TxControl;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;

public class Application
{

    public static void main(String[] args) throws Exception
    {
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);

        System.out.println("Context ready");
    }
}