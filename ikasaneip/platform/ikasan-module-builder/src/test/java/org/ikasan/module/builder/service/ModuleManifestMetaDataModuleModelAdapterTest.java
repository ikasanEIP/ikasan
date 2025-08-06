package org.ikasan.module.builder.service;

import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.AbstractTest;
import org.ikasan.module.builder.model.module.ModuleModel;
import org.junit.Test;

import java.io.IOException;

public class ModuleManifestMetaDataModuleModelAdapterTest extends AbstractTest {

    @Test
    public void test() throws IOException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleManifestMetaDataModuleModelAdapter adapter = new ModuleManifestMetaDataModuleModelAdapter();

        ModuleModel model = adapter.adapt(ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData), "templates/scaffolding/test");

        System.out.println(model);
    }

    @Test
    public void test_complex() throws IOException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaDataComplex.json");
        ModuleManifestMetaDataModuleModelAdapter adapter = new ModuleManifestMetaDataModuleModelAdapter();

        ModuleModel model = adapter.adapt(ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData), "templates/scaffolding/test");

        System.out.println(model);
    }
}
