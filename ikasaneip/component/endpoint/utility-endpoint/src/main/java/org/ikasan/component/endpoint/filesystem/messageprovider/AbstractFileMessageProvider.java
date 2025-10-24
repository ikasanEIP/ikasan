package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.component.endpoint.EndpointListener;

import java.io.IOException;

public abstract class AbstractFileMessageProvider implements EndpointListener<String, IOException> {

    private static final String FQN_PATH_SEPARATOR_LINUX = "/";
    
    /**
     * Retrieves a FileMatcher based on the provided parameters.
     *
     * @param filePath the file path to search within. If null or empty, assumes the files are fully qualified.
     * @param filename the name of the file to search for.
     * @param dynamicFileName whether the filename is dynamic or static.
     * @param ignoreFileNameWhistScanning whether to ignore filename while scanning.
     * @param directoryDepth the depth of the directory tree to walk.
     * @param filenameSpelExpression the SpEL expression for the filename.
     * @param filePathSpelExpression the SpEL expression for the file path.
     * @param followSymbolicLinks whether to follow symbolic links.
     * @return a FileMatcher instance based on the provided parameters.
     */
    protected FileMatcher getFileMatcher(String filePath, String filename, boolean dynamicFileName,
                                         boolean ignoreFileNameWhistScanning, int directoryDepth,
                                         String filenameSpelExpression, String filePathSpelExpression,
                                         boolean followSymbolicLinks)
    {
        String path;
        String name;

        if(filePath == null || filePath.isEmpty()) {
            // assume files are fully qualified
            filename = modifyPathForUnix(filename);

            int lastIndexOffullPath;

            lastIndexOffullPath = filename.lastIndexOf(FQN_PATH_SEPARATOR_LINUX);

            path = filename.substring(0,lastIndexOffullPath);
            name = filename.substring(++lastIndexOffullPath);
        }
        else {
            path = modifyPathForUnix(filePath);
            name = filename;
        }

        if (dynamicFileName)
        {
            return new DynamicFileMatcher(
                ignoreFileNameWhistScanning,
                path,
                name,
                directoryDepth,
                this,
                filenameSpelExpression,
                filePathSpelExpression,
                followSymbolicLinks);
        }
        else
        {
            return new FileMatcher(ignoreFileNameWhistScanning,
                path,
                name,
                directoryDepth,
                this,
                followSymbolicLinks);
        }
    }

    /**
     * Modifies the provided file path for Unix-style systems by applying necessary adjustments.
     *
     * @param filePath the file path to be modified
     * @return the modified file path with appropriate adjustments
     */
    protected String modifyPathForUnix(String filePath) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (!isWindows && !filePath.startsWith("/") && !filePath.startsWith(".")) {
            // assume relative reference and prefix accordingly
            filePath = "./" + filePath;
        }

        return filePath;
    }

    @Override
    public abstract void onMessage(String s);

    @Override
    public abstract void onException(IOException e);

    @Override
    public abstract boolean isActive();
}
