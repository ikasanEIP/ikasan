package org.ikasan.module.migration;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

public class MavenProjectBuilder {

    private final Invoker invoker;

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
            InvocationResult result = invoker.execute(request);
            return result.getExitCode() == 0;
        } catch (MavenInvocationException e) {
            // Handle exception
            e.printStackTrace();
            return false;
        }
    }
}
