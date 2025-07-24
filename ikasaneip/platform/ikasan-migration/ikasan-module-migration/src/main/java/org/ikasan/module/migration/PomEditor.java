package org.ikasan.module.migration;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class PomEditor {

    /**
     * Adds a new dependency to the provided POM file. The dependency is added only if it does not already exist in the POM file.
     *
     * @param pomFile The POM file to which the dependency should be added.
     * @param groupId The group ID of the dependency.
     * @param artifactId The artifact ID of the dependency.
     * @param version The version of the dependency.
     * @throws IOException If an I/O exception occurs during file operations.
     * @throws XmlPullParserException If an error occurs in parsing XML.
     */
    public static void addDependency(File pomFile, String groupId, String artifactId, String version)
        throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));

        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        Exclusion exclusion = new Exclusion();
        exclusion.setGroupId("org.ikasan");
        exclusion.setArtifactId("ikasan-configuration-service");
        dependency.addExclusion(exclusion);
        exclusion = new Exclusion();
        exclusion.setGroupId("org.ikasan");
        exclusion.setArtifactId("ikasan-topology");
        dependency.addExclusion(exclusion);

        if(!model.getDependencies().stream().filter(d -> d.getArtifactId().equals(artifactId) &&
            d.getGroupId().equals(groupId) &&
            d.getVersion().equals(version)).findFirst().isPresent()) {
            model.addDependency(dependency);
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileWriter(pomFile), model);
    }

    public static void removeDependency(File pomFile, String groupId, String artifactId)
        throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));

        AtomicReference<Dependency> dependencyToRemove = new AtomicReference<>();
        model.getDependencies().forEach(dependency -> {
            if(dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
               dependencyToRemove.set(dependency);
            }
        });

        if(dependencyToRemove.get() != null) {
            model.removeDependency(dependencyToRemove.get());
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileWriter(pomFile), model);
    }
}
