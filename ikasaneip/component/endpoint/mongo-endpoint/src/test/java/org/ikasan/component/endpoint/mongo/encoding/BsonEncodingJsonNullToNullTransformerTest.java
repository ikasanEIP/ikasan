package org.ikasan.component.endpoint.mongo.encoding;

import net.sf.json.JSONNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class BsonEncodingJsonNullToNullTransformerTest
{
    private BsonEncodingJsonNullToNullTransformer bsonEncodingJsonNullToNullTransformer;

    @BeforeEach
    void setup()
    {
        bsonEncodingJsonNullToNullTransformer = new BsonEncodingJsonNullToNullTransformer();
    }

    @Test
    void testJsonNullToNull()
    {
        assertNull(bsonEncodingJsonNullToNullTransformer.transform(JSONNull.getInstance()),
            "Excpect JSONNull to transform to null");
    }
}
