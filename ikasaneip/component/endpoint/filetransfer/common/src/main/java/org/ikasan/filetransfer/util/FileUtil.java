package org.ikasan.filetransfer.util;

import java.io.File;

public class FileUtil {

    public static String windowsToUnixPathConverter(String res) {
        if (res==null) return null;
        if (File.separatorChar=='\\') {
            // From Windows to Linux/Mac
            String tmp =  res.replace(File.separatorChar,'/');
            if(tmp.length() > 1 && tmp.charAt(1)==':'){
                return "/"+tmp;
            }
            else{
                return tmp;
            }
        }

        return res;

    }

    /**
     * A double slash can happen if a directory has been appended to a base directory in the properties file
     * but the base directory is root
     *
     * @param outputDirectory
     * @return
     */
    public static String removeDoubleSlashIfPresent(String outputDirectory) {
        if(outputDirectory.startsWith("//")){
            return outputDirectory.substring(1);
        }
        return outputDirectory;
    }


}
