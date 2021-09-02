package org.ikasan.monitor;

import org.ikasan.spec.monitor.JobMonitor;
import org.ikasan.spec.monitor.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DefaultJobMonitorImpl<T> extends AbstractMonitorBase<T> implements JobMonitor<T> {

    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(DefaultFlowMonitorImpl.class);

    /** list of notifiers to be informed */
    protected List<Notifier> notifiers = new ArrayList<>();

    /** the jobName we are monitoring */
    private String jobName;

    /**
     * Constructor
     * @param executorService
     */
    public DefaultJobMonitorImpl(ExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void invoke(final T status)
    {
        super.invoke(status, jobName);
    }


    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }
}
