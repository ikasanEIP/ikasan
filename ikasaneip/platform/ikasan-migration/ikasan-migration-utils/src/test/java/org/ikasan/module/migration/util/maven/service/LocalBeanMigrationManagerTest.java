package org.ikasan.module.migration.util.maven.service;

import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.migration.util.maven.AbstractTest;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;

public class LocalBeanMigrationManagerTest extends AbstractTest {

    @Test
    public void test() throws IOException {
        String jsonFile = this.loadDataFile("/data/moduleMetaData.json");

        ModuleManifestMetaData moduleMetaDataManifest = ModuleManifestMetaDataHelper.deserialiseModuleManifest(jsonFile);

        File rootDir = new File(moduleMetaDataManifest.getModuleMetaData().getName());
        rootDir.mkdirs();

        ModuleFileManager moduleFileManager = new ModuleFileManager(rootDir);

        LocalBeanMigrationManager localBeanMigrationManager = new LocalBeanMigrationManager("com.ikasan.sample.spring.boot",
             moduleFileManager, "jms-demo");
        localBeanMigrationManager.migrateSpringBeans(moduleMetaDataManifest);
    }
}
