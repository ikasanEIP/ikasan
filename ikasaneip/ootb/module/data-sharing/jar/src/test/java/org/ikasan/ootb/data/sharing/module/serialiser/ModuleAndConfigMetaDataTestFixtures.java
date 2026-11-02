package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** Combined fixtures with configurations matching the references in each generated module. */
public final class ModuleAndConfigMetaDataTestFixtures {
    private ModuleAndConfigMetaDataTestFixtures() { }

    /** Creates one combined payload with the requested number of modules, each with one flow. */
    public static ModuleAndConfigMetaData createModuleAndConfigMetaData(int moduleCount) {
        return createModuleAndConfigMetaData(moduleCount, 1);
    }

    /** Counts refer to modules inside the single payload and flows inside each module. */
    public static ModuleAndConfigMetaData createModuleAndConfigMetaData(int moduleCount, int flowsPerModule) {
        if (moduleCount < 0 || flowsPerModule < 0) {
            throw new IllegalArgumentException("Module and flow counts must not be negative");
        }
        List<ModuleMetaData> modules = new ArrayList<>();
        List<ConfigurationMetaData> configurations = new ArrayList<>();
        for (int id = 1; id <= moduleCount; id++) {
            ModuleMetaData module = ModuleMetaDataTestFixtures.createModuleMetaData(id, flowsPerModule);
            modules.add(module);
            configurations.addAll(ConfigurationMetaDataTestFixtures.createConfigurationsForModule(module));
        }
        return new ModuleAndConfigMetaData(modules, configurations);
    }

    public static void assertModuleAndConfigMetaDataEqual(ModuleAndConfigMetaData expected,
                                                         ModuleAndConfigMetaData actual) {
        if (expected == null) {
            assertNull("moduleAndConfigMetaData", actual);
            return;
        }
        assertNotNull("moduleAndConfigMetaData", actual);
        ModuleMetaDataTestFixtures.assertModuleMetaDatasEqual(expected.getModules(), actual.getModules());
        ConfigurationMetaDataTestFixtures.assertConfigurationMetaDataEqual(
            expected.getConfigurations(), actual.getConfigurations());
    }
}
