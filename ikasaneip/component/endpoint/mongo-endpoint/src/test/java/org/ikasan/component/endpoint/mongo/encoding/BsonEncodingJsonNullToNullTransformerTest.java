package org.ikasan.component.endpoint.mongo.encoding;

import static org.junit.Assert.assertNull;
import net.sf.json.JSONNull;

import org.junit.Before;
import org.junit.Test;

public class BsonEncodingJsonNullToNullTransformerTest
{
    private BsonEncodingJsonNullToNullTransformer bsonEncodingJsonNullToNullTransformer;

    @Before
    public void setup()
    {
        bsonEncodingJsonNullToNullTransformer = new BsonEncodingJsonNullToNullTransformer();
    }

    @Test
    public void testJsonNullToNull()
    {
        assertNull("Excpect JSONNull to transform to null",
            bsonEncodingJsonNullToNullTransformer.transform(JSONNull.getInstance()));
    }
}
