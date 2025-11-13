package org.ikasan.module.migration;

import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.module.migration.util.maven.model.CompilationFailureMissingClass;
import org.ikasan.module.migration.util.maven.service.LocalBeanMigrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ModuleBuildMigrationHelper {
    private static Logger logger = LoggerFactory.getLogger(ModuleBuildMigrationHelper.class);
    private LocalBeanMigrationManager localBeanMigrationManager;
    private ModuleFileManager moduleFileManager;

    private int buildIterations = 5;

    public ModuleBuildMigrationHelper(LocalBeanMigrationManager localBeanMigrationManager, ModuleFileManager moduleFileManager) {
        this.localBeanMigrationManager = localBeanMigrationManager;
        this.moduleFileManager = moduleFileManager;
    }

    public void runBuild() throws IOException {
        MavenProjectBuilder builder = new MavenProjectBuilder(System.getenv("M2_HOME"));

        // Format the java code using spotless
        builder.build(this.moduleFileManager.getProjectRootDirectory()
            , "spotless:apply");

        boolean buildPass = false;
        int buildIteration = 0;
        while(!buildPass) {
            if (!builder.build(this.moduleFileManager.getProjectRootDirectory()
                , "clean install")) {
                for (CompilationFailureMissingClass compilationFailureMissingClass : builder.getMissingClassList()) {
                    logger.info("Copying missing dependency - " + compilationFailureMissingClass.getSymbol());
                    this.localBeanMigrationManager.copyMissingDependency(compilationFailureMissingClass.getSymbol());
                }
            }
            else {
                buildPass = true;
            }
            buildIteration++;
            if(buildIteration >= buildIterations) {
                throw new RuntimeException("Number of build iterations exceeded!");
            }
        }
    }
}
