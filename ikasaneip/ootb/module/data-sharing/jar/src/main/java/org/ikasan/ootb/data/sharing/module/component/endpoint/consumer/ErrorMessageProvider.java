package org.ikasan.ootb.data.sharing.module.component.endpoint.consumer;

import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageConsumer;
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.ErrorConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.search.SearchResults;
import org.quartz.JobExecutionContext;

import java.time.Clock;
import java.util.List;

public class ErrorMessageProvider implements CallBackMessageProvider<Boolean>,
    ConfiguredResource<ErrorConsumerConfiguration> {

    private final DataSharingRestService dataSharingRestService;
    private final Clock clock;
    private ErrorConsumerConfiguration configuration;
    private String configurationId;
    private CallBackMessageConsumer callBackMessageConsumer;
    private Long pendingCheckpoint;

    /**
     * Constructs an ErrorMessageProvider with the specified DataSharingRestService dependency.
     *
     * @param dataSharingRestService the DataSharingRestService instance used for querying error data
     */
    public ErrorMessageProvider(DataSharingRestService dataSharingRestService) {
        this(dataSharingRestService, Clock.systemUTC());
    }

    // A fixed clock lets tests distinguish historical windows from the current window.
    ErrorMessageProvider(DataSharingRestService dataSharingRestService, Clock clock) {
        this.dataSharingRestService = dataSharingRestService;
        this.clock = clock;
    }

    @Override
    public Boolean invoke(JobExecutionContext jobExecutionContext) throws EndpointException {
        try {
            long window = configuration.getQueryWindowMilliseconds();
            int batchSize = configuration.getBatchSize();
            if (window <= 0 || batchSize <= 0) {
                throw new IllegalArgumentException("queryWindowMilliseconds and batchSize must be positive");
            }
            long lag = configuration.getSafetyLagMilliseconds();
            long lookback = configuration.getInitialLookbackMilliseconds();
            if (lag < 0 || lookback < 0 || configuration.getQueryTimestamp() < -1) {
                throw new IllegalArgumentException("Lag and lookback must be nonnegative; queryTimestamp must be -1 or nonnegative");
            }
            long cutoff = Math.subtractExact(clock.millis(), lag);
            if (configuration.getQueryTimestamp() == -1) {
                // Choose the initial position once and save it before any REST calls.
                // Saved checkpoints (including an explicit epoch value) take precedence.
                publishWithCheckpoint(List.of(), Math.max(0, Math.subtractExact(cutoff, lookback)));
            }
            long from = configuration.getQueryTimestamp();
            long to = Math.addExact(from, window);
            // Do not advance through a window that can still receive current events.
            // false returns the eager consumer to its normal cron schedule.
            if (to > cutoff) {
                return false;
            }

            // Dashboard range endpoints are inclusive. Represent [from, to) by querying
            // through to - 1 on BOTH count and page requests, avoiding boundary duplicates.
            long inclusiveEnd = to - 1;
            long count = dataSharingRestService.countErrors(from, inclusiveEnd, configuration.getModuleNames());
            if (count < 0) {
                throw new IllegalStateException("Dashboard returned a negative error count");
            }

            if (count == 0) {
                // An empty callback runs the normal flow and persists dynamic configuration.
                // The outbound error producer discards this empty checkpoint batch.
                publishWithCheckpoint(List.of(), to);
            }
            for (long offset = 0; offset < count; offset += batchSize) {
                SearchResults<ErrorOccurrence> results = dataSharingRestService.queryErrors(
                    from, inclusiveEnd, configuration.getModuleNames(), batchSize, Math.toIntExact(offset));
                List<ErrorOccurrence> events = results == null ? null : results.getResultList();
                long expectedSize = Math.min(batchSize, count - offset);
                // Count and page queries are not a snapshot. Fail without advancing if
                // retention or concurrent writes change the count; a retry queries afresh.
                // Equal counts still cannot detect replacement/reordering of records.
                if (events == null || results.getTotalNumberOfResults() != count || events.size() != expectedSize) {
                    throw new IllegalStateException("Dashboard error page does not match the counted results");
                }

                // Completion depends on delivering ALL counted records, not receiving
                // a short page: exact batch multiples must also advance the window.
                if (offset + events.size() == count) {
                    publishWithCheckpoint(events, to);
                } else {
                    callBackMessageConsumer.invoke(events);
                }
            }

            // Continue eagerly only while another complete historical window exists.
            return window <= cutoff - to;
        }
        catch (Exception e) {
            throw new EndpointException(e);
        }
    }

    private void publishWithCheckpoint(List<ErrorOccurrence> events, long nextTimestamp) {
        long previousTimestamp = configuration.getQueryTimestamp();
        int previousOffset = configuration.getOffset();
        pendingCheckpoint = nextTimestamp;
        try {
            // Ikasan reloads configuration before invoking the producer. The producer
            // applies this pending position after enqueueing, then Ikasan persists it.
            callBackMessageConsumer.invoke(events);
        } catch (RuntimeException e) {
            configuration.setQueryTimestamp(previousTimestamp);
            configuration.setOffset(previousOffset);
            throw e;
        } finally {
            pendingCheckpoint = null;
        }
    }

    /** Called by the queue producer after delivery, inside the normal flow invocation. */
    public void applyPendingCheckpoint() {
        if (pendingCheckpoint != null) {
            configuration.setOffset(0);
            configuration.setQueryTimestamp(pendingCheckpoint);
        }
    }

    @Override
    public void setCallBackMessageConsumer(CallBackMessageConsumer callBackMessageConsumer) {
        this.callBackMessageConsumer = callBackMessageConsumer;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public ErrorConsumerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(ErrorConsumerConfiguration configuration) {
        this.configuration = configuration;
    }
}
