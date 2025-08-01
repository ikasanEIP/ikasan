package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.spec.component.endpoint.Broker;

public class ExceptionGenerationgBroker implements Broker {


    @Override
    public Object invoke(Object payload) {
        // TODO: Implement custom logic for ExceptionGenerationgBroker invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking ExceptionGenerationgBroker");
        return null;
    }

}