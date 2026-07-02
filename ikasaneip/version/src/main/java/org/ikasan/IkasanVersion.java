package org.ikasan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class IkasanVersion {
    private static Logger logger = LoggerFactory.getLogger(IkasanVersion.class);

    private static String IKASAN_VERSION = "UNKNOWN";

    static {

        Class theClass = org.ikasan.spec.module.Module.class;

        // Find the path of the compiled class
        String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString();
        logger.debug("Class: " + classPath);

        // The "!" only appears when the class was loaded out of a packaged jar (jar:file:...!/path/to/Class.class).
        // In a Maven reactor build, a downstream module can instead resolve org.ikasan.spec.module.Module straight
        // from an upstream module's target/classes directory (workspace resolution) rather than its installed jar,
        // giving a plain file:/.../target/classes/... URL with no "!". That used to make lastIndexOf("!") return -1
        // and substring(0, -1) throw a StringIndexOutOfBoundsException here in the static initialiser, which
        // permanently poisons this class for the rest of the JVM (every later usage fails with
        // NoClassDefFoundError) - seen intermittently depending on whether the build is a full reactor run or
        // resumed with -rf from a later module. Guard it instead of assuming a jar is always involved.
        int jarSeparatorIndex = classPath.lastIndexOf("!");
        if (jarSeparatorIndex < 0) {
            logger.debug("Class [{}] was not loaded from a jar (no manifest to read); leaving version as [{}]",
                classPath, IKASAN_VERSION);
        }
        else {
            // Find the path of the lib which includes the class
            String libPath = classPath.substring(0, jarSeparatorIndex);
            logger.debug("Lib:   " + libPath);

            // Find the path of the file inside the lib jar
            String filePath = libPath + "!/META-INF/MANIFEST.MF";
            logger.debug("File:  " + filePath);

            try {
                // We look at the manifest file, getting two attributes out of it
                Manifest manifest = new Manifest(new URL(filePath).openStream());
                Attributes attr = manifest.getMainAttributes();
                logger.debug("Implementation-Version: " + attr.getValue("Implementation-Version"));
                if(attr.getValue("Implementation-Version") != null) {
                    IKASAN_VERSION = attr.getValue("Implementation-Version");
                }
                else {
                    logger.warn(String.format("Could not load manifest from from location [%s]. It appears that Implementation-Version is not available" +
                        " in the manifest file!", libPath));
                }
            }
            catch (Exception e) {
                logger.warn(String.format("Could not load manifest from from location [%s] due to exception [%s]"
                    , libPath, e.getMessage()), e);
            }
        }
    }
    public static String getVersion() {
        return IKASAN_VERSION;
    }
}
