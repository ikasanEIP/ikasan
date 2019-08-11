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

}
