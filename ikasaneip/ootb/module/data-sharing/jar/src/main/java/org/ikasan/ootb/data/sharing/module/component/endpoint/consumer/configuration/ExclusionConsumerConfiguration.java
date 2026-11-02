package org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration;

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;

import java.util.List;

public class ExclusionConsumerConfiguration extends ScheduledConsumerConfiguration {
    /** Module names to extract exclusions for. Null or empty means no module filter on the dashboard. */
    private List<String> moduleNames;

    /**
     * Inclusive start of the next query window, in Unix epoch milliseconds, using the
     * dashboard's harvest-received timestamp (not the original error event timestamp).
     * Set an explicit nonnegative value to choose the initial starting date/time; 0
     * means 1970-01-01T00:00:00Z. The default -1 initializes from the lookback once.
     * After initialization this is the saved checkpoint, restored when the flow restarts.
     */
    private long queryTimestamp = -1;

    /**
     * History to request on first initialization only, when queryTimestamp is -1.
     * The start becomes max(0, now - safetyLagMilliseconds - initialLookbackMilliseconds).
     * Defaults to one day; must be nonnegative. Does not override a saved or explicit
     * timestamp and does not infer the dashboard's retention period.
     */
    private long initialLookbackMilliseconds = 24 * 60 * 60 * 1000L;

    /**
     * Delay behind the current clock before a complete window is eligible for extraction.
     * Defaults to one minute; must be nonnegative. Allows for dashboard indexing delays
     * and clock differences, but cannot guarantee capture of arbitrarily late records.
     */
    private long safetyLagMilliseconds = 60 * 1000L;

    /**
     * Duration of each logical window [queryTimestamp, queryTimestamp + duration).
     * Defaults to 100 seconds; must be positive. Since dashboard endpoints include both
     * bounds, REST requests use the logical end minus one millisecond. This duration
     * controls the data range, not the polling schedule inherited from the superclass.
     */
    private long queryWindowMilliseconds = 100000;

    /** Maximum exclusion records requested per page within a window. Defaults to 100; must be positive. */
    private int batchSize = 100;

    /**
     * Retained for configuration compatibility. The provider currently starts paging
     * at zero on each invocation and uses a local offset; this field is not a saved
     * page cursor. It is reset to zero when publishing a checkpoint, so failed windows
     * may replay pages that were already delivered.
     */
    private int offset = 0;

    public long getInitialLookbackMilliseconds() {
        return initialLookbackMilliseconds;
    }

    public void setInitialLookbackMilliseconds(long initialLookbackMilliseconds) {
        this.initialLookbackMilliseconds = initialLookbackMilliseconds;
    }

    public long getSafetyLagMilliseconds() {
        return safetyLagMilliseconds;
    }

    public void setSafetyLagMilliseconds(long safetyLagMilliseconds) {
        this.safetyLagMilliseconds = safetyLagMilliseconds;
    }

    public List<String> getModuleNames() {
        return moduleNames;
    }

    public void setModuleNames(List<String> moduleNames) {
        this.moduleNames = moduleNames;
    }

    public long getQueryTimestamp() {
        return queryTimestamp;
    }

    public void setQueryTimestamp(long queryTimestamp) {
        this.queryTimestamp = queryTimestamp;
    }

    public long getQueryWindowMilliseconds() {
        return queryWindowMilliseconds;
    }

    public void setQueryWindowMilliseconds(long queryWindowMilliseconds) {
        this.queryWindowMilliseconds = queryWindowMilliseconds;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
