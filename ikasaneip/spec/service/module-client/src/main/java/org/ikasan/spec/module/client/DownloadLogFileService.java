package org.ikasan.spec.module.client;

import java.util.Map;

public interface DownloadLogFileService {

    /**
     * Rest call to get the list of log files from the target module server
     * @param contextUrl url to the module
     * @return a Map of string
     *  key - filename
     *  value - location of the file in the file directory of the target module server
     */
    Map<String, String> listLogFiles(String contextUrl);

    /**
     * Rest call to download the log file from the target module server
     * @param contextUrl url to the module
     * @param fullFilePath full path name of the log file to download
     * @return byte array of the file
     */
    byte[] downloadLogFile(String contextUrl, String fullFilePath);
}
