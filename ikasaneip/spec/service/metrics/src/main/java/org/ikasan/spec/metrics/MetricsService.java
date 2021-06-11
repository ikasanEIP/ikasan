package org.ikasan.spec.metrics;

import java.util.List;

public interface MetricsService<ENTITY>
{
    /**
     * Get all metrics for a given time range.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<ENTITY> getMetrics(long startTime, long endTime);

    /**
     * Get all metrics for a given time range for a module.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<ENTITY> getMetrics(String moduleName, long startTime, long endTime);

    /**
     * Get all metrics for a given time range for a flow.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<ENTITY> getMetrics(String moduleName, String flowName, long startTime, long endTime);

}
