package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.model.MigrationType;
import org.ikasan.cli.shell.migration.service.MigrationService;
import org.ikasan.cli.shell.operation.MigrationOperation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class H2CheckMigrationAggregateOperationTest {

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
    public void test_h2_database_migration_aggregate_operation_esb_database_sucesss() {
//        mockery.checking(new Expectations()
//        {
//            {
//                exactly(1).of(migrationService).find(with(any(String.class)), with(any(String.class)), with(any(String.class)));
//                will(returnValue(null));
//            }
//        });

        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation = new H2DatabaseMigrationAggregateOperation("java -Dmodule.name=${module.name} -cp ./lib/migration/h2-[source.h2.version].jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/migrated.sql",
            "java -Dmodule.name=${module.name} -cp ./lib/migration/h2-[target.h2.version].jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./db-migration/post-processed-migrated.sql",
            "java -Dmodule.name=${module.name} -cp ./lib/migration/h2-[target.h2.version].jar org.h2.tools.RunScript -url jdbc:h2:[database.path]-new -user [database.username] -password [database.password] -script ./lib/migration/liquibase-changelog-contents.sql,",
                "java -Dmodule.name=${module.name} -cp ./lib/migration/h2-[target.h2.version].jar org.h2.tools.Script -url jdbc:h2:[database.path] -user [database.username] -password [database.password] -script ./db-migration/test.sql",
            "",
            "",
            "sa",
            "sa",
            "./src/test/resources/migration/h2_1_4_200_sample_db/esb",
            "./db-migration",
            "migrated.sql",
            "post-processed-migrated.sql",
            ".",
            true
            );

        String result = h2DatabaseMigrationAggregateOperation.execute();

        Assert.assertNotNull(result);
    }


}
