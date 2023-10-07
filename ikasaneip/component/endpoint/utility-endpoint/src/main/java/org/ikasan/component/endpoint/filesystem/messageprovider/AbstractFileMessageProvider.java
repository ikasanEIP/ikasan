package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.component.endpoint.EndpointListener;

import java.io.IOException;

public abstract class AbstractFileMessageProvider implements EndpointListener<String, IOException> {

    private static final String FQN_PATH_SEPARATOR_LINUX = "/";
    
    protected FileMatcher getFileMatcher(String filePath, String filename, boolean dynamicFileName,
                                         boolean ignoreFileNameWhistScanning, int directoryDepth, String spelExpression)
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
                spelExpression);
        }
        else
        {
            return new FileMatcher(ignoreFileNameWhistScanning,
                path,
                name,
                directoryDepth,
                this);
        }
    }

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
