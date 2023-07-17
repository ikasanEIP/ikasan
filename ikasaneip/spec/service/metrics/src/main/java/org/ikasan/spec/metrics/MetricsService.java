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
    List<ENTITY> getMetrics(long startTime, long endTime);

    /**
     * Get all metrics for a given time range for a module.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<ENTITY> getMetrics(String moduleName, long startTime, long endTime);

    /**
     * Get all metrics for a given time range for a flow.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<ENTITY> getMetrics(String moduleName, String flowName, long startTime, long endTime);

    /**
     * Get metrics for a given time range, with offset and limit.
     *
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<ENTITY> getMetrics(long startTime, long endTime, int offset, int limit);

    /**
     * Get number of metrics records for a given time range.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    long count(long startTime, long endTime);

    /**
     * Get metrics for a given time range for a module, with offset and limit.
     *
     * @param moduleName
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<ENTITY> getMetrics(String moduleName, long startTime, long endTime, int offset, int limit);

    /**
     * Get number of metrics records for a given time range for a module.
     *
     * @param moduleName
     * @param startTime
     * @param endTime
     * @return
     */
    long count(String moduleName, long startTime, long endTime);

    /**
     * Get metrics for a given time range for a flow, with offset and limit.
     *
     * @param moduleName
     * @param flowName
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<ENTITY> getMetrics(String moduleName, String flowName, long startTime, long endTime, int offset, int limit);

    /**
     * Get number of metrics records for a given time range for a flow.
     *
     * @param moduleName
     * @param flowName
     * @param startTime
     * @param endTime
     * @return
     */
    long count(String moduleName, String flowName, long startTime, long endTime);
}
