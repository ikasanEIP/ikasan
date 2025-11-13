package org.ikasan.module.migration;

import freemarker.template.TemplateException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FlowTestInspectorTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_against_real_project() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("sftp-jms-im-4-1-x", "./src/test/modules/archetype/sftp-jms-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/sftp-jms-im-4-1-x-working"
            , "JmsToSftpFlowTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_db_jms_im_4_1_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("db-jms-im-4-1-x", "./src/test/modules/archetype/db-jms-im-4-1-x"
            , "com.ikasan.sample", "./target/modules/migrated/db-jms-im-4-1-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_db_jms_im_3_3_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("db-jms-im-3-3-x", "./src/test/modules/archetype/db-jms-im-3-3-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/db-jms-im-3-3-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_file_system_jms_im_4_1_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("filesystem-jms-im-4-1-x", "./src/test/modules/archetype/filesystem-jms-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/filesystem-jms-im-4-1-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_file_system_jms_im_3_3_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("filesystem-jms-im-3-3-x", "./src/test/modules/archetype/filesystem-jms-im-3-3-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/filesystem-jms-im-3-3-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_ftp_jms_im_4_1_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("ftp-jms-im-4-1-x", "./src/test/modules/archetype/ftp-jms-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/ftp-jms-im-4-1-x-working"
            , "FtpToJmsFlowTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_ftp_jms_im_3_3_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("ftp-jms-im-3-3-x", "./src/test/modules/archetype/ftp-jms-im-3-3-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/ftp-jms-im-3-3-x-working"
            , "FtpToJmsFlowTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_jms_im_4_1_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("jms-im-4-1-x", "./src/test/modules/archetype/jms-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/jms-im-4-1-x-working"
            , "JmsSampleFlowTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_jms_im_3_3_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("jms-im-3-3-x", "./src/test/modules/archetype/jms-im-3-3-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/jms-im-3-3-x-working"
            , "JmsSampleFlowTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_vanilla_im_4_1_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("vanilla-im-4-1-x", "./src/test/modules/archetype/vanilla-im-4-1-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/vanilla-im-4-1-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }

    @Test
    public void test_vanilla_im_3_3_x_archetype_migration() throws IOException, XmlPullParserException, TemplateException {
        ModuleMigration migration = new ModuleMigration("vanilla-im-3-3-x", "./src/test/modules/archetype/vanilla-im-3-3-x"
            , "com.ikasan.sample.spring.boot", "./target/modules/migrated/vanilla-im-3-3-x-working"
            , "ApplicationTest", "com.sample");
        migration.migrate();
    }
}
