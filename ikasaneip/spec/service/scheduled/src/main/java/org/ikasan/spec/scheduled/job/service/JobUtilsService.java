package org.ikasan.spec.scheduled.job.service;

public interface JobUtilsService {

    /**
     * Kill a job running based on its pid.
     *
     * @param contextUrl
     * @param pid
     * @param forcibly
     */
    public void killJob(String contextUrl, long pid, boolean forcibly);
}
