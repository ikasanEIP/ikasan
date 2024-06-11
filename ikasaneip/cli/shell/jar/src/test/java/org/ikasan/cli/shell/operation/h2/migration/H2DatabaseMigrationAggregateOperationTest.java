package org.ikasan.cli.shell.operation.h2.migration;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class H2DatabaseMigrationAggregateOperationTest {
    @BeforeEach
    public void setup() throws IOException {
        FileUtils.copyFile(new File("./src/test/resources/migration/h2_1_4_200_sample_db/esb.mv.db"),
            new File("./target/h2_1_4_200_sample_db/esb.mv.db"));
        FileUtils.copyFile(new File("./src/test/resources/migration/h2_1_4_200_sample_db/non-esb.mv.db"),
            new File("./target/h2_1_4_200_sample_db/non-esb.mv.db"));
    }

    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File("./db-migration"));
        FileUtils.deleteDirectory(new File("./migration_manifest"));
        FileUtils.deleteDirectory(new File("./target/h2_1_4_200_sample_db"));
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_NON_esb_database_success_and_not_run_second_time() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_1_4_200_sample_db/non-esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            false,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"result\":\"Successfully executed command [java -Dmodule.name=moduleName " +
            "-cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:./target/h2_1_4_200_sample_db/non-esb " +
            "-user sa -password sa -script ./db-migration/migrated.sql]\\r\\nSuccessfully perform migration process post process." +
            " Pre process file [./db-migration/migrated.sql] - Post process file [./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -" +
            "url jdbc:h2:./target/h2_1_4_200_sample_db/non-esb-new -user sa -password sa -script ./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully backed up source database from [./target/h2_1_4_200_sample_db/non-esb.mv.db] to [./target/h2_1_4_200_sample_db/non-esb.mv.db-backup-1.4"));

        // We try to run migration second time.
        result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"The migration process has been run already and will not be re-run!\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_esb_database_success_and_not_run_second_time() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
                "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_1_4_200_sample_db/esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true,
            300
            );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"result\":\"Successfully executed command [java -Dmodule.name=moduleName " +
            "-cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:./target/h2_1_4_200_sample_db/esb " +
            "-user sa -password sa -script ./db-migration/migrated.sql]\\r\\nSuccessfully perform migration process post process. Pre " +
            "process file [./db-migration/migrated.sql] - Post process file [./db-migration/post-processed-migrated.sql]\\r\\nSuccessfully " +
            "executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript " +
            "-url jdbc:h2:./target/h2_1_4_200_sample_db/esb-new -user sa -password sa -script ./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript " +
            "-url jdbc:h2:./target/h2_1_4_200_sample_db/esb-new -user sa -password sa -script ../sql/migration/liquibase-changelog-contents.sql]" +
            "\\r\\nSuccessfully backed up source database from [./target/h2_1_4_200_sample_db/esb.mv.db] to " +
            "[./target/h2_1_4_200_sample_db/esb.mv.db-backup-1.4"));

        // We try to run migration second time.
        result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"The migration process has been run already and will not be re-run!\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_both_esb_and_NON_esb_database_success_and_not_run_second_time() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_1_4_200_sample_db/non-esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            false,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"result\":\"Successfully executed command [java -Dmodule.name=moduleName " +
            "-cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:./target/h2_1_4_200_sample_db/non-esb " +
            "-user sa -password sa -script ./db-migration/migrated.sql]\\r\\nSuccessfully perform migration process post process." +
            " Pre process file [./db-migration/migrated.sql] - Post process file [./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -" +
            "url jdbc:h2:./target/h2_1_4_200_sample_db/non-esb-new -user sa -password sa -script ./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully backed up source database from [./target/h2_1_4_200_sample_db/non-esb.mv.db] to [./target/h2_1_4_200_sample_db/non-esb.mv.db-backup-1.4"));

        // We try to run migration second time.
        result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"The migration process has been run already and will not be re-run!\"}", result);

        h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_1_4_200_sample_db/esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true,
            300
        );

        result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"result\":\"Successfully executed command [java -Dmodule.name=moduleName " +
            "-cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:./target/h2_1_4_200_sample_db/esb " +
            "-user sa -password sa -script ./db-migration/migrated.sql]\\r\\nSuccessfully perform migration process post process. Pre " +
            "process file [./db-migration/migrated.sql] - Post process file [./db-migration/post-processed-migrated.sql]\\r\\nSuccessfully " +
            "executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript " +
            "-url jdbc:h2:./target/h2_1_4_200_sample_db/esb-new -user sa -password sa -script ./db-migration/post-processed-migrated.sql]\\r\\n" +
            "Successfully executed command [java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript " +
            "-url jdbc:h2:./target/h2_1_4_200_sample_db/esb-new -user sa -password sa -script ../sql/migration/liquibase-changelog-contents.sql]" +
            "\\r\\nSuccessfully backed up source database from [./target/h2_1_4_200_sample_db/esb.mv.db] to " +
            "[./target/h2_1_4_200_sample_db/esb.mv.db-backup-1.4"));

        // We try to run migration second time.
        result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"The migration process has been run already and will not be re-run!\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_esb_database_migration_not_needed_as_already_on_target_version() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_2_2_224_sample_db/esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"Database file[./target/h2_2_2_224_sample_db/esb.mv.db] was not found " +
            "so there is nothing to migrate. A new empty database will be created when the module is next started.\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_NON_esb_database_migration_not_needed_as_already_on_target_version() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_2_2_224_sample_db/non-esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            false,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"Database file[./target/h2_2_2_224_sample_db/non-esb.mv.db] was not " +
            "found so there is nothing to migrate. A new empty database will be created when the module is next started.\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_esb_database_migration_not_needed_as_database_does_not_exist() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_2_2_224_sample_db/esb_not_exits",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"Database file[./target/h2_2_2_224_sample_db/esb_not_exits.mv.db] was" +
            " not found so there is nothing to migrate. A new empty database will be created when the module is next started.\"}", result);
    }

    @Test
    public void test_h2_database_migration_aggregate_operation_NON_esb_database_migration_not_needed_as_database_does_not_exist() {
        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ../sql/migration/liquibase-changelog-contents.sql",
            "java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "1.4.200",
            "2.2.224",
            "sa",
            "sa",
            "./target/h2_2_2_224_sample_db/esb_not_exits",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true,
            300
        );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"result\":\"Database file[./target/h2_2_2_224_sample_db/esb_not_exits.mv.db] was " +
            "not found so there is nothing to migrate. A new empty database will be created when the module is next started.\"}", result);
    }

}
