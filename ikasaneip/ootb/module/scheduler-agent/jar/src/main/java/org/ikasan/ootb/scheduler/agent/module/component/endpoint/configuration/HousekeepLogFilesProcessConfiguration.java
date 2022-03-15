package org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HousekeepLogFilesProcessConfiguration {

    @Value( "${housekeep.log.files.process.log-folder}" )
    private String logFolder;
    @Value( "${housekeep.log.files.process.ttl.days}" )
    private int timeToLive = 30;
    @Value( "${housekeep.log.files.process.should-archive}" )
    private boolean shouldArchive = false;
    @Value( "${housekeep.log.files.process.should-move}" )
    private boolean shouldMove = false;
    @Value( "${housekeep.log.files.process.move-folder}" )
    private String folderToMove;

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isShouldArchive() {
        return shouldArchive;
    }

    public void setShouldArchive(boolean shouldArchive) {
        this.shouldArchive = shouldArchive;
    }

    public boolean isShouldMove() {
        return shouldMove;
    }

    public void setShouldMove(boolean shouldMove) {
        this.shouldMove = shouldMove;
    }

    public String getFolderToMove() {
        return folderToMove;
    }

    public void setFolderToMove(String folderToMove) {
        this.folderToMove = folderToMove;
    }
}
