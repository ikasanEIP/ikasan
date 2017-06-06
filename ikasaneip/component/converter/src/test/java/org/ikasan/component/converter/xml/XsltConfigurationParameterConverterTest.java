/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.converter.xml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test cases for XsltConfigurationParameterConverter
 *
 * @author Ikasan Development Team
 */
public class XsltConfigurationParameterConverterTest
{

    private XsltConfigurationParameterConverter converter;

    private TestConfiguration testConfiguration;

    @Before
    public void setup()
    {
        converter = new XsltConfigurationParameterConverter();
        testConfiguration = new TestConfiguration();
    }

    @Test
    public void testConvertPrimitives() throws Exception
    {
        String testString = "A test xslt string";

        testConfiguration.setXsltParamTestString(testString);
        testConfiguration.setXsltParamTestInt(123);
        testConfiguration.setXsltParamTestBoolean(true);
        testConfiguration.setXsltParamTestLong(9999999999999L);

        Map<String, String> result = converter.convert(testConfiguration);

        Assert.assertEquals(4, result.size());

        Assert.assertEquals(testString, result.get("testString"));
        Assert.assertEquals("123", result.get("testInt"));
        Assert.assertEquals("true", result.get("testBoolean"));
        Assert.assertEquals("9999999999999", result.get("testLong"));

    }

    @Test
    public void testConvertMap() throws Exception
    {
        Map<String, String> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", "value3");

        testConfiguration.setXsltParamTestMap(testMap);

        Map<String, String> result = converter.convert(testConfiguration);

        Assert.assertEquals(4, result.size());

        String actualMapXml = result.get("testMap");

        String entry1 = "<entry key=\"key1\" value=\"value1\"/>";
        String entry2 = "<entry key=\"key2\" value=\"value2\"/>";
        String entry3 = "<entry key=\"key3\" value=\"value3\"/>";

        Assert.assertTrue(actualMapXml.contains(entry1));
        Assert.assertTrue(actualMapXml.contains(entry2));
        Assert.assertTrue(actualMapXml.contains(entry3));

        Assert.assertTrue(actualMapXml.replace(entry1, "").replace(entry2, "").replace(entry3, "").equals("<map></map>"));
    }

    @Test
    public void testConvertList() throws Exception
    {
        List<String> testList = new ArrayList<>();
        testList.add("value1");
        testList.add("value2");
        testList.add("value3");

        testConfiguration.setXsltParamTestList(testList);

        Map<String, String> result = converter.convert(testConfiguration);

        Assert.assertEquals(4, result.size());

        String actualListXml = result.get("testList");

        String expectedListXml = "<list><value>value1</value><value>value2</value><value>value3</value></list>";

        Assert.assertEquals(expectedListXml, actualListXml);

    }

    @Test
    public void testMethodMissingResultsInRuntimeException()
    {
        InvalidConfiguration invalidConfiguration = new InvalidConfiguration();

        try
        {
            converter.convert(invalidConfiguration);
            Assert.fail("Configuration field has no getter - should throw a RuntimeException");
        }
        catch (RuntimeException e)
        {
            Assert.assertEquals("error occurred introspecting XsltConverterConfiguration instance for field name: xsltParamHasNoGetter", e.getMessage());
        }
    }

    public class TestConfiguration extends XsltConverterConfiguration
    {

        // these fields should be converted
        private String xsltParamTestString;

        private long xsltParamTestLong;

        private int xsltParamTestInt;

        private boolean xsltParamTestBoolean;

        private Map xsltParamTestMap;

        private List xsltParamTestList;

        private Long xsltParamTestLongClass;

        private Integer xsltParamTestIntegerClass;

        private Integer xlstParamTestBooleanClass;

        // these fields should be ignored
        private String anotherString;

        private long anotherLong;

        private int anotherInt;

        public String getXsltParamTestString()
        {
            return xsltParamTestString;
        }

        public void setXsltParamTestString(String xsltParamTestString)
        {
            this.xsltParamTestString = xsltParamTestString;
        }

        public long getXsltParamTestLong()
        {
            return xsltParamTestLong;
        }

        public void setXsltParamTestLong(long xsltParamTestLong)
        {
            this.xsltParamTestLong = xsltParamTestLong;
        }

        public int getXsltParamTestInt()
        {
            return xsltParamTestInt;
        }

        public void setXsltParamTestInt(int xsltParamTestInt)
        {
            this.xsltParamTestInt = xsltParamTestInt;
        }

        public boolean isXsltParamTestBoolean()
        {
            return xsltParamTestBoolean;
        }

        public void setXsltParamTestBoolean(boolean xsltParamTestBoolean)
        {
            this.xsltParamTestBoolean = xsltParamTestBoolean;
        }

        public Map getXsltParamTestMap()
        {
            return xsltParamTestMap;
        }

        public void setXsltParamTestMap(Map xsltParamTestMap)
        {
            this.xsltParamTestMap = xsltParamTestMap;
        }

        public List getXsltParamTestList()
        {
            return xsltParamTestList;
        }

        public void setXsltParamTestList(List xsltParamTestList)
        {
            this.xsltParamTestList = xsltParamTestList;
        }

        public Long getXsltParamTestLongClass()
        {
            return xsltParamTestLongClass;
        }

        public void setXsltParamTestLongClass(Long xsltParamTestLongClass)
        {
            this.xsltParamTestLongClass = xsltParamTestLongClass;
        }

        public Integer getXsltParamTestIntegerClass()
        {
            return xsltParamTestIntegerClass;
        }

        public void setXsltParamTestIntegerClass(Integer xsltParamTestIntegerClass)
        {
            this.xsltParamTestIntegerClass = xsltParamTestIntegerClass;
        }

        public Integer getXlstParamTestBooleanClass()
        {
            return xlstParamTestBooleanClass;
        }

        public void setXlstParamTestBooleanClass(Integer xlstParamTestBooleanClass)
        {
            this.xlstParamTestBooleanClass = xlstParamTestBooleanClass;
        }

        public String getAnotherString()
        {
            return anotherString;
        }

        public void setAnotherString(String anotherString)
        {
            this.anotherString = anotherString;
        }

    }

    public class InvalidConfiguration extends XsltConverterConfiguration
    {
        private String xsltParamHasNoGetter;
    }
}