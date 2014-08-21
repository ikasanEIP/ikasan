package org.ikasan.component.endpoint.mongo.encoding;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class BsonEncodingStringToDateTransformerTest
{
    private BsonEncodingStringToDateTransformer bsonEncodingStringToDateTransformer;

    @Before
    public void setup()
    {
        bsonEncodingStringToDateTransformer = new BsonEncodingStringToDateTransformer();
    }

    @Test
    public void testTransformDateTime() throws ParseException
    {
        String testDateTimeStr = "2014-03-17T16:33:24";
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17T16:33:24");
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(testDateTimeStr);
        assertEquals("The testDateTimeStr should have been transformed to a date", expectedDate, transformedDate);
    }

    @Test
    public void testTransformDateTimeWithWrongFormat() throws ParseException
    {
        String testDateTimeStr = "2014-03-17T16:33:24";
        bsonEncodingStringToDateTransformer.setDateTimeFormatString("zz");
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17T16:33:24");
        assertEquals("The testDateTimeStr should not have been transformed", testDateTimeStr, transformedDate);
    }

    @Test
    public void testTransformDate() throws ParseException
    {
        String testDateStr = "2014-03-17";
        Object transformedDate = bsonEncodingStringToDateTransformer.transform("2014-03-17");
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd").parse(testDateStr);
        assertEquals("The testDateStr should have been transformed to a date", expectedDate, transformedDate);
    }

    @Test
    public void testTransformString()
    {
        String testStr = "31451.45222Z";
        Object transformedNumber = bsonEncodingStringToDateTransformer.transform(testStr);
        assertEquals("The test string should have NOT been transformed", testStr, transformedNumber);
    }
}
