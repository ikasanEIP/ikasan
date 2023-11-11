package org.ikasan.component.endpoint.mongo.encoding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BsonEncodingStringToDateTransformerTest
{
    private BsonEncodingStringToDateTransformer bsonEncodingStringToDateTransformer;

    @BeforeEach
    void setup()
    {
        bsonEncodingStringToDateTransformer = new BsonEncodingStringToDateTransformer();
    }

    @Test
    void testTransformDateTime() throws ParseException
    {
        String testDateTimeStr = "2014-03-17T16:33:24";
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17T16:33:24");
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(testDateTimeStr);
        assertEquals(expectedDate, transformedDate, "The testDateTimeStr should have been transformed to a date");
    }

    @Test
    void testTransformDateTimeWithWrongFormat() throws ParseException
    {
        String testDateTimeStr = "2014-03-17T16:33:24";
        bsonEncodingStringToDateTransformer.setDateTimeFormatString("zz");
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17T16:33:24");
        assertEquals(testDateTimeStr, transformedDate, "The testDateTimeStr should not have been transformed");
    }

    @Test
    void testTransformDate() throws ParseException
    {
        String testDateStr = "2014-03-17";
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17");
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd").parse(testDateStr);
        assertEquals(expectedDate, transformedDate, "The testDateStr should have been transformed to a date");
    }

    @Test
    void testTransformString()
    {
        String testStr = "31451.45222Z";
        Object transformedNumber = bsonEncodingStringToDateTransformer.transform(testStr);
        assertEquals(testStr, transformedNumber, "The test string should have NOT been transformed");
    }
}
