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
package org.ikasan.web.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

/**
 * Unit test class supporting <class>JacksonMapStringConverter</class>
 *
 * @author Ikasan Developmnet Team
 */
class JacksonMapStringConverterTest
{
    private JacksonMapStringConverter uut;

    @BeforeEach
    void setup()
    {
        uut = new JacksonMapStringConverter();
    }

    /**
     * Test null to map.
     */
    @Test
    void test_map_converter_with_null_value() throws Exception
    {
        Object result = uut.convertTargetToSourceClass(null, Map.class);
        assertNull(result, "result should be null");
    }

    /**
     * Test string to map.
     */
    @Test
    void test_map_converter_with_valid_string() throws Exception
    {
        Object result = uut.convertTargetToSourceClass("{\"one\":\"1\",\"two\":\"2\",\"three\":\"3\"}", Map.class);
        assertTrue(result instanceof Map, "result should be an instance of a map");
        Map<String, String> resultMap = (Map) result;
        assertEquals(resultMap.get("one"), String.valueOf("1"), "map contains entry for one");
        assertEquals(resultMap.get("two"), String.valueOf("2"), "map contains entry for two");
        assertEquals(resultMap.get("three"), String.valueOf("3"), "map contains entry for three");
    }

    /**
     * Test string to map.
     */
    @Test
    void test_map_converter_with_one_null_value() throws Exception
    {
        Object result = uut.convertTargetToSourceClass("{\"one\":null,\"two\":\"2\",\"three\":\"3\"}", Map.class);
        assertTrue(result instanceof Map, "result should be an instance of a map");
        Map<String, String> resultMap = (Map) result;
        assertTrue(resultMap.get("one") == null, "map contains entry for one");
        assertEquals(resultMap.get("two"), String.valueOf("2"), "map contains entry for two");
        assertEquals(resultMap.get("three"), String.valueOf("3"), "map contains entry for three");
    }

    /**
     * Test string to map.
     */
    @Test
    void test_map_converter_with_one_entry() throws Exception
    {
        Object result = uut.convertTargetToSourceClass("{\"one\":\"1\"}", Map.class);
        assertTrue(result instanceof Map, "result should be an instance of a map");
        Map<String, String> resultMap = (Map) result;
        assertEquals(resultMap.get("one"), String.valueOf("1"), "map contains entry for one");
    }

    /**
     * Test string to map.
     */
    @Test
    void test_map_converter_with_no_fields() throws Exception
    {
        Object result = uut.convertTargetToSourceClass("{}", Map.class);
        assertTrue(result instanceof Map, "result should be an instance of a map");
        Map<String, String> resultMap = (Map) result;
        assertEquals(0, resultMap.size(), "map contains entry for one");
    }

    /**
     * Test string to map.
     */
    @Test
    void test_map_converter_with_non_string_value() throws Exception
    {
        assertThrows(ClassCastException.class, () -> {
            uut.convertTargetToSourceClass("{\"one\":\"1\",\"two\":true,\"three\":\"3\"}", Map.class);
        });
    }

    @Test
    void test_map_converter_map_to_string() throws Exception
    {
        Map<String, String> map = new HashMap<>();
        map.put("key", "{\"one\":{\"$gt \":\"50\",\"$lte\":\"100\"}}");
        Object result = uut.convertSourceToTargetClass(map, String.class);
        assertEquals("{\"key\":\"{\\\"one\\\":{\\\"$gt \\\":\\\"50\\\",\\\"$lte\\\":\\\"100\\\"}}\"}", result);
    }

    @Test
    void test_map_converter_with_space() throws Exception
    {
        Object result = uut.convertTargetToSourceClass("{\"one\":\"1 \"}", Map.class);
        assertEquals("1 ", ((Map)result).get("one"));
    }
}
