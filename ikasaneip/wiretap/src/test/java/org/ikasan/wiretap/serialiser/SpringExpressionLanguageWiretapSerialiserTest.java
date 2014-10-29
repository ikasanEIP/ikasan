package org.ikasan.wiretap.serialiser;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SpringExpressionLanguageWiretapSerialiserTest
{
    private static class PayloadMap {
        
        private Map<String,String> map= new HashMap<String,String>();
   
        public void addEntry(String payloadKey, String payload){
            this.map.put(payloadKey, payload);
        }

        public String getEntry(String payloadKey){
            return this.map.get(payloadKey);
        }
    }
    
    @Test
    public void testSerialiseWithoutEventName()
    {
        PayloadMap thingToSerialize = new PayloadMap();
        thingToSerialize.addEntry("payload", "payloadText");
        String springExpression = "#root.getEntry('payload')";
        SpringExpressionLanguageWiretapSerialiser<PayloadMap> serialiser = new SpringExpressionLanguageWiretapSerialiser<>(springExpression);
        assertEquals("The serializer should have extracted the payload from the map", "payloadText", serialiser.serialise(thingToSerialize));
    }

    @Test
    public void testSerialiseWithEventName()
    {
        PayloadMap thingToSerialize = new PayloadMap();
        thingToSerialize.addEntry("payload", "payloadText");
        String springExpression = "#msg.getEntry('payload')";
        SpringExpressionLanguageWiretapSerialiser<PayloadMap> serialiser = new SpringExpressionLanguageWiretapSerialiser<>("msg", springExpression);
        assertEquals("The serializer should have extracted the payload from the map", "payloadText", serialiser.serialise(thingToSerialize));
    }
}
