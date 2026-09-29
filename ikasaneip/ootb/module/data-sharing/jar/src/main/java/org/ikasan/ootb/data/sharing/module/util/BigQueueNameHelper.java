package org.ikasan.ootb.data.sharing.module.util;

public class BigQueueNameHelper {

    public static String getQueueName(String dashboardName, String entity) {
        if(dashboardName == null || dashboardName.isEmpty()) {
            throw new IllegalArgumentException("dashboardName cannot be null or empty!");
        }
        if(entity == null || entity.isEmpty()) {
            throw new IllegalArgumentException("entity cannot be null or empty!");
        }

        return dashboardName.replaceAll(" ", "-")
            + "-" + entity.replaceAll(" ", "") + "-queue";
    }
}
