package com.ikasan.sample.spring.boot.builderpattern.components.translator;

import org.ikasan.spec.component.transformation.Translator;

public class MyCustomTranslator implements Translator {


    @Override
    public void translate(Object payload) {
        // TODO: Implement custom logic for MyCustomTranslator invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking MyCustomTranslator");
    }

}