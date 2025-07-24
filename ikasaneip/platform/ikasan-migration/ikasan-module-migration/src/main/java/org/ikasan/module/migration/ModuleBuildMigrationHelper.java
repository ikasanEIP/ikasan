package org.ikasan.module.migration;

import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.module.migration.util.maven.model.CompilationFailureMissingClass;
import org.ikasan.module.migration.util.maven.service.LocalBeanMigrationManager;

import java.io.IOException;

public class ModuleBuildMigrationHelper {
    private LocalBeanMigrationManager localBeanMigrationManager;
    private ModuleFileManager moduleFileManager;

    public ModuleBuildMigrationHelper(LocalBeanMigrationManager localBeanMigrationManager, ModuleFileManager moduleFileManager) {
        this.localBeanMigrationManager = localBeanMigrationManager;
        this.moduleFileManager = moduleFileManager;
    }

    public void runBuild() throws IOException {
        MavenProjectBuilder builder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        boolean buildPass = false;
        while(!buildPass) {
            if (!builder.build(this.moduleFileManager.getProjectRootDirectory()
                , "clean install")) {
                for (CompilationFailureMissingClass compilationFailureMissingClass : builder.getMissingClassList()) {
                    this.localBeanMigrationManager.copyMissingDependency(compilationFailureMissingClass.getSymbol());
                }
            }
            else {
                buildPass = true;
            }
        }
    }
}
