package com.ikasan.component.factory;



public interface ComponentFactory<T> {

    T create (String nameSuffix, String configPrefix, String factoryConfigPrefix);
}
