package org.ikasan.manifest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DependencyHelper {
    public static List<MavenCoordinates> getRuntimeDependencies() throws IOException {
        List<String> results = new ArrayList<>();
        String classpath = System.getProperty("java.class.path");
        if (classpath != null && !classpath.isEmpty()) {
            List<String> classpathEntries = Arrays.asList(classpath.split(File.pathSeparator));
            for (String entry : classpathEntries) {
                if (entry.toLowerCase().endsWith(".jar")) {
                    results.add(entry);
                }
            }
        }
        return getMavenCoordinates(results);
    }

    public boolean classExistsOnClasspath(URL[] jars, String className) {
        try {
            URLClassLoader classLoader = new URLClassLoader(jars, ClassLoader.getSystemClassLoader());
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static List<MavenCoordinates> getMavenCoordinates(List<String> jars) throws IOException {
        List<MavenCoordinates> mavenCoordinates = new ArrayList<>();
        for(String jarPath: jars) {
            try (JarFile jarFile = new JarFile(jarPath)) {
                for (JarEntry entry : java.util.Collections.list(jarFile.entries())) {
                    String entryName = entry.getName();
                    if (entryName.startsWith("META-INF/maven/") && entryName.endsWith("/pom.properties")) {
                        Properties props = new Properties();
                        try (InputStream is = jarFile.getInputStream(entry)) {
                            props.load(is);
                        }
                        String groupId = props.getProperty("groupId");
                        String artifactId = props.getProperty("artifactId");
                        String version = props.getProperty("version");

                        if (groupId != null && artifactId != null && version != null) {
                            mavenCoordinates.add(new MavenCoordinates(groupId, artifactId, version));
                        }
                    }
                }
            }
        }

        return mavenCoordinates;
    }

    public static class MavenCoordinates {
        private final String groupId;
        private final String artifactId;
        private final String version;

        public MavenCoordinates(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }
    }
}