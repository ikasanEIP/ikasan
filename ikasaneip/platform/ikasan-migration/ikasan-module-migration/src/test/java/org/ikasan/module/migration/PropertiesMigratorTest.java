package org.ikasan.module.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PropertiesMigratorTest {

    @Test
    public void test() throws IOException {
        PropertiesMigrator.comparePropertiesFiles
            ("./src/test/modules/archetype/jms-im-3-3-x/jar/src/test/resources/application.properties",
                "./target/modules/migrated/jms-im-3-3-x-working/jms-im-3-3-x/scaffolding/src/test/resources/application.properties");
    }
}
