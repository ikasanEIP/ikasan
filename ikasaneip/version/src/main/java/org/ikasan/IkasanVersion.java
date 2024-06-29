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

        // Find the path of the lib which includes the class
        String libPath = classPath.substring(0, classPath.lastIndexOf("!"));
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
    public static String getVersion() {
        return IKASAN_VERSION;
    }
}
