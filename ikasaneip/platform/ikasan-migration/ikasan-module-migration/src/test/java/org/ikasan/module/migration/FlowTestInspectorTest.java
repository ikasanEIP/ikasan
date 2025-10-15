package org.ikasan.module.migration;

import freemarker.template.TemplateException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class   FlowTestInspectorTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_against_real_project() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("sftp-jms-im-4-1-x", "/Users/mick/workspace/archetype/sftp-jms-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "/Users/mick/workspace/migration-working"
            , "JmsToSftpFlowTest", "org.ikasan");
        migration.migrate();
    }
}
