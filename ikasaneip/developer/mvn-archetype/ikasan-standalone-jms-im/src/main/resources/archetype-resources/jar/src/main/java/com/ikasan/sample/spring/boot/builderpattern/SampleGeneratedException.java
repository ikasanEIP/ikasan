package com.ikasan.sample.spring.boot.builderpattern;

/**
 * Created by majean on 09/10/2017.
 */
public class SampleGeneratedException extends RuntimeException
{
    public SampleGeneratedException(Throwable e){
        super(e);
    }

    public SampleGeneratedException(String m){
        super(m);
    }


}
