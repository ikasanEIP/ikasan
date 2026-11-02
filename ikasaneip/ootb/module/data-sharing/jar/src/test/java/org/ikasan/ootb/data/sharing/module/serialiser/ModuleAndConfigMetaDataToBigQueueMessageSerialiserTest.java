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
package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationParameterMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.ModuleMetaDataImpl;
import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataTestFixtures.*;
import static org.junit.Assert.*;

/** Verifies both metadata lists survive transport together in one BigQueue message. */
public class ModuleAndConfigMetaDataToBigQueueMessageSerialiserTest {
    private ModuleAndConfigMetaDataToBigQueueMessageSerialiser serialiser;

    @Before
    public void setUp() {
        serialiser = new ModuleAndConfigMetaDataToBigQueueMessageSerialiser();
    }

    @Test
    public void test_round_trip_single_module_and_its_configurations() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        assertEquals(1, expected.getModules().size());
        assertEquals(7, expected.getConfigurations().size());
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_serialise_multiple_modules_inside_one_message() throws Exception {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(3, 2);
        byte[] bytes = serialiser.serialise(expected);
        var message = new ObjectMapper().readTree(bytes).get("message");
        assertTrue(message.isObject());
        assertEquals(3, message.get("modules").size());
        assertEquals(39, message.get("configurations").size());
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(bytes));
    }

    @Test
    public void test_round_trip_empty_lists() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(0);
        assertTrue(expected.getModules().isEmpty());
        assertTrue(expected.getConfigurations().isEmpty());
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_round_trip_large_batch() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1000, 2);
        assertEquals(1000, expected.getModules().size());
        assertEquals(13000, expected.getConfigurations().size());
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_round_trip_module_only_payload() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        expected.getConfigurations().clear();
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_round_trip_configuration_only_payload() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        expected.getModules().clear();
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_special_characters_in_both_metadata_types() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        String value = "Description with special chars: \n\t\r\"'\\{}[]<>";
        expected.getModules().get(0).setDescription(value);
        ConfigurationMetaDataImpl configuration = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(
            expected.getConfigurations().get(0).getConfigurationId());
        configuration.setDescription(value);
        configuration.getParameters().get(0).setValue(value);
        expected.getConfigurations().set(0, configuration);
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_long_descriptions_in_both_metadata_types() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        String value = "X".repeat(10000);
        expected.getModules().get(0).setDescription(value);
        ConfigurationMetaDataImpl configuration = ConfigurationMetaDataTestFixtures.createConfigurationMetaData(
            expected.getConfigurations().get(0).getConfigurationId());
        configuration.setDescription(value);
        expected.getConfigurations().set(0, configuration);
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_round_trip_null_fields() {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(1);
        var module = expected.getModules().get(0);
        module.setDescription(null);
        module.setContext(null);
        module.setHost(null);
        module.setIkasanVersion(null);
        module.setName(null);
        module.setPort(null);
        module.setProtocol(null);
        module.setUrl(null);
        module.setVersion(null);
        expected.getConfigurations().set(0, ConfigurationMetaDataTestFixtures
            .createConfigurationMetaDataWithNullFields(module.getConfiguredResourceId()));
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_round_trip_null_lists() {
        ModuleAndConfigMetaData expected = new ModuleAndConfigMetaData(null, null);
        assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
    }

    @Test
    public void test_deserialised_metadata_uses_dashboard_dtos() {
        ModuleAndConfigMetaData restored = serialiser.deserialise(serialiser.serialise(createModuleAndConfigMetaData(1)));
        assertTrue(restored.getModules().get(0) instanceof ModuleMetaDataImpl);
        assertTrue(restored.getConfigurations().get(0) instanceof ConfigurationMetaDataImpl);
        ConfigurationMetaDataImpl configuration = (ConfigurationMetaDataImpl) restored.getConfigurations().get(0);
        assertTrue(configuration.getParameters().get(0) instanceof ConfigurationParameterMetaDataImpl);
    }

    @Test(expected = RuntimeException.class)
    public void test_deserialise_null_bytes_throws_exception() {
        serialiser.deserialise(null);
    }

    @Test(expected = RuntimeException.class)
    public void test_deserialise_invalid_bytes_throws_exception() {
        serialiser.deserialise("invalid json".getBytes(StandardCharsets.UTF_8));
    }

    @Test(expected = RuntimeException.class)
    public void test_deserialise_empty_bytes_throws_exception() {
        serialiser.deserialise(new byte[0]);
    }

    @Test
    public void test_concurrent_serialization() throws Exception {
        ModuleAndConfigMetaData expected = createModuleAndConfigMetaData(2);
        var executor = Executors.newFixedThreadPool(4);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(() -> {
                    assertModuleAndConfigMetaDataEqual(expected, serialiser.deserialise(serialiser.serialise(expected)));
                    return null;
                });
            }
            for (var result : executor.invokeAll(tasks)) {
                result.get();
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
