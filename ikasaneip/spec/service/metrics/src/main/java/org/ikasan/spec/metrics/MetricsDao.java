package org.ikasan.spec.metrics;

import org.ikasan.spec.history.FlowInvocationMetric;

import java.util.List;

/**
 * Interface for Metrics DAO operations.
 * Created by Ikasan Development Team.
 */
public interface MetricsDao
{
    String METRIC_ENTITY_TYPE = "metric";

    /**
     * Save an individual FlowInvocationMetric entity
     *
     * @param event
     */
    void save(FlowInvocationMetric event);

    /**
     * Save a list of FlowInvocationMetric entities.
     * @param events
     */
    void save(List<FlowInvocationMetric> events);

    /**
     * Get metrics for a given time range.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<FlowInvocationMetric> getMetrics(long startTime, long endTime);

    /**
     * Get metrics for a given time range with pagination.
     *
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<FlowInvocationMetric> getMetrics(long startTime, long endTime, int offset, int limit);

    /**
     * Count metrics for a given time range.
     *
     * @param startTime
     * @param endTime
     * @return
     */
    long count(long startTime, long endTime);

    /**
     * Get metrics for a given module and time range.
     *
     * @param moduleName
     * @param startTime
     * @param endTime
     * @return
     */
    List<FlowInvocationMetric> getMetrics(String moduleName, long startTime, long endTime);

    /**
     * Get metrics for a given module and time range with pagination.
     *
     * @param moduleName
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<FlowInvocationMetric> getMetrics(String moduleName, long startTime, long endTime, int offset, int limit);

    /**
     * Count metrics for a given module and time range.
     *
     * @param moduleName
     * @param startTime
     * @param endTime
     * @return
     */
    long count(String moduleName, long startTime, long endTime);

    /**
     * Get metrics for a given module, flow and time range.
     *
     * @param moduleName
     * @param flowName
     * @param startTime
     * @param endTime
     * @return
     */
    List<FlowInvocationMetric> getMetrics(String moduleName, String flowName, long startTime, long endTime);

    /**
     * Get metrics for a given module, flow and time range with pagination.
     *
     * @param moduleName
     * @param flowName
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return
     */
    List<FlowInvocationMetric> getMetrics(String moduleName, String flowName, long startTime, long endTime, int offset, int limit);

    /**
     * Count metrics for a given module, flow and time range.
     *
     * @param moduleName
     * @param flowName
     * @param startTime
     * @param endTime
     * @return
     */
    long count(String moduleName, String flowName, long startTime, long endTime);
}
