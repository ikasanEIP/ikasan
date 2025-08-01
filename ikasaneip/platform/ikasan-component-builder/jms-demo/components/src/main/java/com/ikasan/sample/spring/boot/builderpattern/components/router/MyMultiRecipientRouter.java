package com.ikasan.sample.spring.boot.builderpattern.components.router;

import org.ikasan.spec.component.routing.MultiRecipientRouter;
import java.util.List;
import java.lang.String;

public class MyMultiRecipientRouter implements MultiRecipientRouter<String> {


    @Override
    public List<String> route(String payload) {
        // TODO: Implement custom logic for MyMultiRecipientRouter invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking MyMultiRecipientRouter");
        return null;
    }

}