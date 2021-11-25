package org.ikasan.component.endpoint.bigqueue.serialiser;

import org.ikasan.spec.serialiser.Serialiser;

public class SimpleStringSerialiser implements Serialiser<String, byte[]> {

    @Override
    public byte[] serialise(String source) {
        return source.getBytes();
    }

    @Override
    public String deserialise(byte[] source) {
        return new String(source);
    }
}
