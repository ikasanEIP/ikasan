package org.ikasan.module.migration.util.maven;

import org.apache.maven.shared.invoker.*;
import org.ikasan.module.migration.util.maven.handler.MissingClassInvocationOutputHandler;
import org.ikasan.module.migration.util.maven.model.CompilationFailureMissingClass;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MavenProjectBuilder {
    public static final String COMPILATION_FAILURE = "Compilation failure";
    public static final String CANNOT_FIND_SYMBOL = "cannot find symbol";
    public static final String SYMBOL = "symbol:";
    public static final String LOCATION = "location:";
    public static final String ERROR = "[ERROR]";

    private final Invoker invoker;
    private MissingClassInvocationOutputHandler missingClassInvocationOutputHandler;

    /**
     * Constructs a new instance of MavenProjectBuilder with the specified Maven home directory.
     *
     * @param mavenHome The directory path to the Maven home. Can be null or empty.
     */
    public MavenProjectBuilder(String mavenHome) {
        this.invoker = new DefaultInvoker();
        if (mavenHome != null && !mavenHome.isEmpty()) {
            this.invoker.setMavenHome(new File(mavenHome));
        }
    }

    /**
     * Builds a Maven project located in the specified directory using the provided Maven command.
     *
     * @param projectDirectory The directory where the Maven project is located.
     * @param mavenCommand The Maven command to execute for building the project.
     * @return true if the build was successful (exit code 0), false otherwise.
     */
    public boolean build(File projectDirectory, String mavenCommand) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(projectDirectory, "pom.xml"));
        request.setGoals(Collections.singletonList(mavenCommand));

        try {
            this.missingClassInvocationOutputHandler
                = new MissingClassInvocationOutputHandler();
            request = request.setOutputHandler(missingClassInvocationOutputHandler);
            InvocationResult result = invoker.execute(request);
            return result.getExitCode() == 0;
        } catch (MavenInvocationException e) {
            // Handle exception
            e.printStackTrace();
            return false;
        }
    }

    public List<CompilationFailureMissingClass> getMissingClassList() {
        return this.missingClassInvocationOutputHandler.getMissingClassList();
    }
}
