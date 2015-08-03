package org.ikasan.component.converter.xml;

import junit.framework.TestCase;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by elliga on 29/07/2015.
 */
public class XsltConfigurationParameterConverterTest {

    private XsltConfigurationParameterConverter converter;

    private TestConfiguration testConfiguration;

    @Before
    public void setup() {
        converter = new XsltConfigurationParameterConverter();
        testConfiguration = new TestConfiguration();
    }

    @Test
    public void testConvertPrimitives() throws Exception {
        String testString = "A test xslt string";

        testConfiguration.setXsltParamTestString(testString);
        testConfiguration.setXsltParamTestInt(123);
        testConfiguration.setXsltParamTestBoolean(true);
        testConfiguration.setXsltParamTestLong(9999999999999l);

        Map<String, String> result = converter.convert(testConfiguration);

        Assert.assertEquals(4, result.size());

        Assert.assertEquals(testString, result.get("testString"));
        Assert.assertEquals("123", result.get("testInt"));
        Assert.assertEquals("true", result.get("testBoolean"));
        Assert.assertEquals("9999999999999", result.get("testLong"));

    }

    @Test
    public void testConvertMap() throws Exception {
        Map testMap = new HashedMap();
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
    public void testConvertList() throws Exception {
        List testList = new ArrayList();
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
    public void testMethodMissingResultsInRuntimeException() {
        InvalidConfiguration invalidConfiguration = new InvalidConfiguration();

        try {
            converter.convert(invalidConfiguration);
            Assert.fail("Configuration field has no getter - should throw a RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("error occurred introspecting XsltConverterConfiguration instance for field name: xsltParamHasNoGetter", e.getMessage());
        }
    }

    public class TestConfiguration extends XsltConverterConfiguration{

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

        public String getXsltParamTestString() {
            return xsltParamTestString;
        }

        public void setXsltParamTestString(String xsltParamTestString) {
            this.xsltParamTestString = xsltParamTestString;
        }

        public long getXsltParamTestLong() {
            return xsltParamTestLong;
        }

        public void setXsltParamTestLong(long xsltParamTestLong) {
            this.xsltParamTestLong = xsltParamTestLong;
        }

        public int getXsltParamTestInt() {
            return xsltParamTestInt;
        }

        public void setXsltParamTestInt(int xsltParamTestInt) {
            this.xsltParamTestInt = xsltParamTestInt;
        }

        public boolean isXsltParamTestBoolean() {
            return xsltParamTestBoolean;
        }

        public void setXsltParamTestBoolean(boolean xsltParamTestBoolean) {
            this.xsltParamTestBoolean = xsltParamTestBoolean;
        }

        public Map getXsltParamTestMap() {
            return xsltParamTestMap;
        }

        public void setXsltParamTestMap(Map xsltParamTestMap) {
            this.xsltParamTestMap = xsltParamTestMap;
        }

        public List getXsltParamTestList() {
            return xsltParamTestList;
        }

        public void setXsltParamTestList(List xsltParamTestList) {
            this.xsltParamTestList = xsltParamTestList;
        }

        public Long getXsltParamTestLongClass() {
            return xsltParamTestLongClass;
        }

        public void setXsltParamTestLongClass(Long xsltParamTestLongClass) {
            this.xsltParamTestLongClass = xsltParamTestLongClass;
        }

        public Integer getXsltParamTestIntegerClass() {
            return xsltParamTestIntegerClass;
        }

        public void setXsltParamTestIntegerClass(Integer xsltParamTestIntegerClass) {
            this.xsltParamTestIntegerClass = xsltParamTestIntegerClass;
        }

        public Integer getXlstParamTestBooleanClass() {
            return xlstParamTestBooleanClass;
        }

        public void setXlstParamTestBooleanClass(Integer xlstParamTestBooleanClass) {
            this.xlstParamTestBooleanClass = xlstParamTestBooleanClass;
        }

        public String getAnotherString() {
            return anotherString;
        }

        public void setAnotherString(String anotherString) {
            this.anotherString = anotherString;
        }

    }

    public class InvalidConfiguration extends XsltConverterConfiguration {
        private String xsltParamHasNoGetter;
    }
}