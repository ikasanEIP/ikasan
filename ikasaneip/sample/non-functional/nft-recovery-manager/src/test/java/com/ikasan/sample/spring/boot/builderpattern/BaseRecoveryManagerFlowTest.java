package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.with;
import static org.ikasan.recovery.ScheduledRecoveryManager.RECOVERY_JOB_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BaseRecoveryManagerFlowTest {
    private Logger logger = LoggerFactory.getLogger(BaseRecoveryManagerFlowTest.class);

    @Resource
    protected ErrorReportingService errorReportingService;
    protected IkasanFlowTestRule flowTestRule;
    @Resource
    protected ExclusionManagementService exclusionManagementService;

    @Resource
    @Autowired
    @Qualifier("ikasan.xads")
    private DataSource ikasanxads;

    /**
     * Retrieves the number of currently scheduled recovery jobs in the provided Scheduler.
     *
     * @param scheduler the Scheduler instance from which to retrieve the job keys
     * @return the total number of recovery job keys that are currently scheduled in the Scheduler
     * @throws SchedulerException if there is an issue retrieving the job keys from the scheduler
     */
    protected int getNumberOfCurrentScheduledRecoveryJobs(Scheduler scheduler) throws SchedulerException {
        Set<JobKey> jobKeyList = scheduler.getJobKeys(GroupMatcher.anyGroup());

        List<String> jobKeys = jobKeyList.stream()
            .filter(key -> key.getName().startsWith(RECOVERY_JOB_NAME))
            .map(key -> key.getName())
            .collect(Collectors.toList());

        logger.info("recovery jobs: " + jobKeys);

        logger.info("Number of recovery jobs: " + jobKeys.size());

        return jobKeys.size();
    }

    /**
     * This method clears all data from the database tables that may have been added between tests.
     * It uses the DatabaseHelper class to interact with the database and perform the data clearing operation.
     * Any SQLException that occurs during the database clearing process will be propagated up.
     * It is protected to allow subclasses to access and utilize this method as needed.
     */
    protected void clearDatabase() throws SQLException {
        new DatabaseHelper(ikasanxads).clearDatabase();
    }

    /**
     * Asserts that the number of errors retrieved from the errorReportingService matches the expected number
     *
     * @param expectedNumberOfErrors the expected number of errors to assert against
     */
    protected void assertErrorsWithWait(int expectedNumberOfErrors) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> errors = this.errorReportingService.find(null, null, null, null, null, 1000);
                assertEquals(expectedNumberOfErrors, errors.size());
            });
    }

    /**
     * Asserts that the number of error reporting events found by the errorReportingService
     * is greater than the expected number of errors after waiting for a specific duration.
     *
     * @param expectedNumberOfErrors the expected number of errors that the method should find
     */
    protected void assertErrorsGreaterThanWithWait(int expectedNumberOfErrors) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertTrue(this.errorReportingService.find(null, null, null
                , null, null, 1000).size() > expectedNumberOfErrors));
    }


    /**
     * Asserts that the number of exclusions found matches the expected number within a specified time frame.
     *
     * @param expectedNumberOfExclusions the expected number of exclusions to be found
     */
    protected void assertExclusionsWithWait(int expectedNumberOfExclusions) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 1000);
                assertEquals(expectedNumberOfExclusions, exclusions.size());
            });
    }
}
