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
package org.ikasan.cli.shell.command;

import org.ikasan.cli.shell.operation.h2.migration.H2DatabaseMigrationAggregateOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.List;

/**
 * Process commands for start, query, stop of the H2 process.
 *
 * @author Ikasan Developmnent Team
 */
@Command
public class H2MigrationCommand
{
    private static Logger logger = LoggerFactory.getLogger(H2MigrationCommand.class);

    @Value("${module.name:null}")
    private String moduleName;

    @Value("${h2.script.command:#{null}}")
    private String h2ScriptJavaCommand;

    @Value("${h2.runscript.command:#{null}}")
    private String h2RunScriptJavaCommand;

    @Value("${h2.changelog.runscript.command}")
    private String h2ChangeLogRunScriptJavaCommand;

    @Value("${h2.determine.if.db.file.already.target.version.command}")
    private String determineIfDbFileAlreadyTargetVersionCommand;

    @Value("${h2.logging.file:logs/h2.log}")
    private String h2Log;

    @Value("${supported.h2.migration.source.versions}")
    private List<String> supportedH2MigrationSourceVersions;

    @Value("${supported.h2.migration.target.versions}")
    private List<String> supportedH2MigrationTargetVersions;

    @Value("${persistence.dir}")
    private String persistenceDir;

    @Value("${h2.db.migration.working.directory:./db-migration/}")
    private String dbMigrationWorkingDirectory;

    @Value("${h2.db.migrated.sql.filename:migrated.sql}")
    private String migratedOutputSqlFileName;

    @Value("${h2.db.post.processed.sql.filename:migrated.sql}")
    private String postProcessedOutputSqlFileName;

    @Value("${h2.db.migration.should.run:true}")
    private boolean dbMigrationShouldRun;

    @Value("${h2.db.migration.module.persistence.database.path:#{null}}")
    private String modulePersistenceDatabasePath;

    @Value("${datasource.username:#{null}}")
    private String dbMigrationDatabaseUsername;

    @Value("${datasource.password:#{null}}")
    private String dbMigrationDatabasePassword;

    @Value("${ikasan.shell.migration.forked.process.timeout:300}")
    private long forkedProcessTimeout;

    /**
     * Migrates H2 persistence.
     *
     * @param sourceH2Version The version of the H2 database we are migrating from.
     * @param targetH2Version The version of the H2 database we are migrating to.
     * @param h2User The username of the H2 database to use for the migration.
     * @param h2Password The password of the H2 database to use for the migration.
     * @param databaseLocation The path to the database. The general Ikasan convention [<persistence-dir>/<module-name>-db/esb]
     *                         will be used by default.
     *
     * @return The result of the migration operation.
     */
    @Command(description = "Migrate H2 persistence", group = "Ikasan Commands", command = "migrate-h2")
    public String migrateH2(@Option(description = "The version of the H2 database we are migrating from.", longNames = "source-h2-version", defaultValue = "1.4.200")  String sourceH2Version,
                            @Option(description = "The version of the H2 database we are migrating to.", longNames = "target-h2-version",defaultValue = "2.2.224")  String targetH2Version,
                            @Option(description = "The username of the H2 database to use for the migration.", longNames = "h2-user", defaultValue = "")  String h2User,
                            @Option(description = "The password of the H2 database to use for the migration.", longNames = "h2-password", defaultValue = "")  String h2Password,
                            @Option(description = "The path to the database. The general Ikasan convention [<persistence-dir>/<module-name>-db/esb]" +
                                " will be used by default.", longNames = "h2-database-location",defaultValue = "")  String databaseLocation,
                            @Option(description = "Boolean flag to indicate whether the database being migrated is a " +
                                "core Ikasan ESB database or not.", longNames = "h2-database-is-esb-database",defaultValue = "true") boolean isEsbDatabase) {
        if(!dbMigrationShouldRun) return "H2 DB migration will not run. Property h2.db.migration.should.run is set to false.";

        if(this.modulePersistenceDatabasePath == null || this.modulePersistenceDatabasePath.isEmpty()) {
            throw new RuntimeException("Property h2.db.migration.module.persistence.database.path has not been defined " +
                "and as a result the H2 database migration cannot run. Please see documentation for further details.");
        }

        if((h2User == null || h2User.isEmpty()) && (this.dbMigrationDatabaseUsername == null || this.dbMigrationDatabaseUsername.isEmpty())) {
            throw new RuntimeException("Shell option h2-user and property 'datasource.username' cannot both be null or empty.");
        }

        if((h2Password == null || h2Password.isEmpty()) && (this.dbMigrationDatabasePassword == null || this.dbMigrationDatabasePassword.isEmpty())) {
            throw new RuntimeException("Shell option h2-password and property 'datasource.password' cannot both be null or empty.");
        }

        H2DatabaseMigrationAggregateOperation h2DatabaseMigrationAggregateOperation
            = new H2DatabaseMigrationAggregateOperation(this.h2ScriptJavaCommand, this.h2RunScriptJavaCommand
                , this.h2ChangeLogRunScriptJavaCommand, this.determineIfDbFileAlreadyTargetVersionCommand, sourceH2Version
                , targetH2Version, h2User != null ? h2User : this.dbMigrationDatabaseUsername, h2Password != null ? h2Password : this.dbMigrationDatabasePassword
                , databaseLocation == null || databaseLocation.isEmpty() ? this.modulePersistenceDatabasePath: databaseLocation, this.dbMigrationWorkingDirectory
                , this.migratedOutputSqlFileName, this.postProcessedOutputSqlFileName, this.persistenceDir, isEsbDatabase, this.forkedProcessTimeout);

        return h2DatabaseMigrationAggregateOperation.execute();
    }
}