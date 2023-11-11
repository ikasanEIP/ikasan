/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package org.ikasan.component.factory.spring.common;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanMergeUtilTest {

    private static Logger logger = LoggerFactory.getLogger(BeanMergeUtilTest.class);

    @Test
    void test() throws InvocationTargetException, IllegalAccessException {
        BeanConfiguration sharedConfiguration = new BeanConfiguration();
        BeanConfiguration componentConfiguration = new BeanConfiguration();

        Map<String,String>map = new HashMap<>();
        map.put("sharedMapKey", "sharedMapValue");
        sharedConfiguration.setMapProp(map);
        sharedConfiguration.setStringProp("sharedStringVal");
        sharedConfiguration.setListProp(Arrays.asList("sharedListVal"));
        sharedConfiguration.setAuthenticated(true);

        componentConfiguration.setListProp(Arrays.asList("configListVal"));
        // componentConfiguration.setStringProp("configStringVal");

        BeanMergeUtil.mergeSourceIntoTargetBean( componentConfiguration, sharedConfiguration);
        componentConfiguration = null;
        assertEquals("sharedStringVal", sharedConfiguration.getStringProp());
        assertEquals("sharedMapValue", sharedConfiguration.getMapProp().get("sharedMapKey"));
        assertEquals("configListVal", sharedConfiguration.getListProp().get(0));
    }

    @Test
    void testWithEmptyMapInSource() throws InvocationTargetException, IllegalAccessException {
        BeanConfiguration sharedConfiguration = new BeanConfiguration();
        BeanConfiguration componentConfiguration = new BeanConfiguration();

        Map<String,String>map = new HashMap<>();
        map.put("sharedMapKey", "sharedMapValue");
        sharedConfiguration.setMapProp(map);
        componentConfiguration.setMapProp(new HashMap<>());
        BeanMergeUtil.mergeSourceIntoTargetBean( componentConfiguration, sharedConfiguration);
        componentConfiguration = null;
        assertEquals("sharedMapValue", sharedConfiguration.getMapProp().get("sharedMapKey"));
    }

    @Test
    void testWithEmptyListInSource() throws InvocationTargetException, IllegalAccessException {
        BeanConfiguration sharedConfiguration = new BeanConfiguration();
        BeanConfiguration componentConfiguration = new BeanConfiguration();

        Map<String,String>map = new HashMap<>();
        sharedConfiguration.setListProp(Arrays.asList("sharedListVal"));
        componentConfiguration.setListProp(new ArrayList<>());
        BeanMergeUtil.mergeSourceIntoTargetBean( componentConfiguration, sharedConfiguration);
        componentConfiguration = null;
        assertEquals("sharedListVal", sharedConfiguration.getListProp().get(0));
    }

    /**
     * Test if configuration has shared Boolean property is necessary to ensure this is null - (rather than defaulted to FALSE)
     * or else the overriding configuration will override the shared boolean value with its default value
     * (usually to false)
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    void testBoolean() throws InvocationTargetException, IllegalAccessException {
        BeanConfiguration sharedConfiguration = new BeanConfiguration();
        BeanConfiguration componentConfiguration = new BeanConfiguration();

        sharedConfiguration.setAuthenticated(true);
        BeanMergeUtil.mergeSourceIntoTargetBean( componentConfiguration, sharedConfiguration);
        componentConfiguration = null;
        assertTrue(sharedConfiguration.getAuthenticated());
        assertTrue(sharedConfiguration.isAuthenticated());
    }
}