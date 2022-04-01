package org.ikasan.ootb.scheduler.agent.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaUtilsTestHelper {

    private static Logger logger = LoggerFactory.getLogger(JavaUtilsTestHelper.class);

    public static void main(String[] args) throws InterruptedException {

        for (int i=0;i<1000;i++) {
            logger.info("test count:"+i);
            Thread.sleep(100);
        }
    }

    public static Process exec(Class clazz) throws IOException
    {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = clazz.getName();

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-cp");
        command.add(classpath);
        command.add(className);

        ProcessBuilder builder = new ProcessBuilder(command);
        return builder.inheritIO().start();
    }
}
