package org.ikasan.module.migration;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PomMigrationUtilities {


    /**
     * Adds all dependencies from the source POM file to the target POM file.
     *
     * @param srcPomFile The source POM file to read dependencies from.
     * @param tgtPomFile The target POM file to add dependencies to.
     * @throws IOException If an I/O error occurs while reading or writing the POM files.
     * @throws XmlPullParserException If an error occurs during XML parsing of the POM files.
     */
    public static void migrateDependencies(File srcPomFile, File tgtPomFile)
        throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model srcModel = reader.read(new FileReader(srcPomFile));
        Model tgtModel = reader.read(new FileReader(tgtPomFile));

        srcModel.getDependencies().forEach
            (dependency -> {
                if(!tgtModel.getDependencies().stream().filter(d ->
                    d.getArtifactId().equals(dependency.getArtifactId()) && d.getGroupId().equals(dependency.getGroupId())
                        ).findFirst().isPresent()) {
                    tgtModel.addDependency(dependency);
                }
            });

        srcModel.getProperties().entrySet().forEach(entry -> {
            if(((String)entry.getKey()).startsWith("version") && !tgtModel.getProperties().containsKey(entry.getKey())) {
                tgtModel.getProperties().put(entry.getKey(),entry.getValue());
            }
        });

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileWriter(tgtPomFile), tgtModel);
    }
}
