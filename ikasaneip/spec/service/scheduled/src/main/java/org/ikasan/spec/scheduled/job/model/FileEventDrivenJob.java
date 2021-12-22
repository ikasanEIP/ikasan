package org.ikasan.spec.scheduled.job.model;

public interface FileEventDrivenJob extends QuartzScheduleDrivenJob {

    /**
     * Get the file path of the file we are waiting on in order to raise the event.
     *
     * @return
     */
    String getFilePath();

    /**
     * Set the file path to the file that generates the event.
     *
     * @param path
     */
    void setFilePath(String path);
}
