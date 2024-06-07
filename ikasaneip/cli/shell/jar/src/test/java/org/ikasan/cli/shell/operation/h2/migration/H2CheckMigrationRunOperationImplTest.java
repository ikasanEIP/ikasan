package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.ikasan.cli.shell.migration.service.MigrationService;
import org.ikasan.cli.shell.operation.MigrationOperation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class H2CheckMigrationRunOperationImplTest {

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    final MigrationService migrationService = mockery.mock(MigrationService.class, "mockMigrationService");
    final IkasanMigration ikasanMigration = mockery.mock(IkasanMigration.class, "mockIkasanMigration");

    @Test
    public void test_null_migration_service_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(null, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql")));
    }

    @Test
    public void test_null_migration_type_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(migrationService, null, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql")));
    }

    @Test
    public void test_null_source_version_service_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, null
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql")));
    }

    @Test
    public void test_null_target_version_service_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , null, "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql")));
    }

    @Test
    public void test_null_database_location_service_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", null, "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql")));
    }

    @Test
    public void test_null_command_list_service_exception() {
        assertThrows(IllegalArgumentException.class,
            () -> new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , null));
    }

    @Test
    public void test_migration_required_but_not_yet_run() {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(migrationService).find(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(null));
            }
        });

        H2CheckMigrationRunOperationImpl h2CheckMigrationRunOperation
            = new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql"));

        String result = h2CheckMigrationRunOperation.execute();

        Assert.assertEquals(MigrationOperation.NOT_YET_RUN, result);
    }

    @Test
    public void test_migration_not_required_as_already_run() {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(migrationService).find(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(ikasanMigration));
            }
        });

        H2CheckMigrationRunOperationImpl h2CheckMigrationRunOperation
            = new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_1_4_200_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_1_4_200_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql"));

        String result = h2CheckMigrationRunOperation.execute();

        Assert.assertEquals(MigrationOperation.RUN_PREVIOUSLY, result);
    }

    @Test
    public void test_migration_not_required_database_already_on_target_version() {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(migrationService).save(with(any(IkasanMigration.class)));
                exactly(1).of(migrationService).find(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(null));
            }
        });

        H2CheckMigrationRunOperationImpl h2CheckMigrationRunOperation
            = new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_2_2_224_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_2_2_224_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql"));

        String result = h2CheckMigrationRunOperation.execute();

        Assert.assertEquals(MigrationOperation.NOT_REQUIRED, result);
    }

    @Test
    public void test_migration_not_required_database_does_not_exist_and_will_be_created_new() {
        mockery.checking(new Expectations()
        {
            {
//                exactly(1).of(migrationService).save(with(any(IkasanMigration.class)));
//                will(returnValue(null));
                exactly(1).of(migrationService).find(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(ikasanMigration));
            }
        });

        H2CheckMigrationRunOperationImpl h2CheckMigrationRunOperation
            = new H2CheckMigrationRunOperationImpl(migrationService, MigrationType.H2_MIGRATION, "1.4.200"
            , "2.2.224", "./src/test/resources/migration/h2_2_2_224_sample_db/esb", "ESB"
            , List.of("java -Dmodule.name=moduleName -cp ./src/test/resources/migration/lib/h2-2.2.224.jar " +
            "org.h2.tools.Script -url jdbc:h2:./src/test/resources/migration/h2_2_2_224_sample_db/esb -user sa -password sa " +
            "-script ./db-migration/test.sql"));

        String result = h2CheckMigrationRunOperation.execute();

        Assert.assertEquals(MigrationOperation.RUN_PREVIOUSLY, result);
    }
}
