package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Context<CONTEXT extends Context, CONTEXT_PARAM, JOB extends SchedulerJob, JOB_LOCK extends JobLock> extends Serializable {

    /**
     * Get the name of the context
     * @return
     */
    String getName();

    /**
     * Set the name of the context.
     * @param name
     */
    void setName(String name);

    /**
     * Get the description of the context
     *
     * @return
     */
    String getDescription();

    /**
     * Set the description of the template.
     *
     * @param description
     */
    void setDescription(String description);

    /**
     * Get the timezone that the job plan operates within.
     * @return
     */
    String getTimezone();

    /**
     * Set the timezone that the job plan operates within.
     *
     * @param timezone
     */
    void setTimezone(String timezone);

    /**
     * Get the blackout window cron expressions.
     *
     * @return
     */
    List<String> getBlackoutWindowCronExpressions();

    /**
     * Set the blackout window cron expressions.
     *
     * @param blackoutWindowCronExpressions
     */
    void setBlackoutWindowCronExpressions(List<String> blackoutWindowCronExpressions);

    /**
     * Get the blackout window date time ranges.
     *
     * @return
     */
    Map<Long, Long> getBlackoutWindowDateTimeRanges();

    /**
     * Set the blackout window date time ranges.
     *
     * @param blackoutWindowDateTimeRanges
     */
    void setBlackoutWindowDateTimeRanges(Map<Long, Long> blackoutWindowDateTimeRanges);

    /**
     * Get the context parameters.
     *
     * @return
     */
    List<CONTEXT_PARAM> getContextParameters();

    /**
     * Set the context parameters.
     *
     * @param contextParameters
     */
    void setContextParameters(List<CONTEXT_PARAM> contextParameters) ;

    /**
     * Get the scheduled jobs
     *
     * @return
     */
    List<JOB> getScheduledJobs();

    /**
     * Set the scheduled jobs
     *
     * @param scheduledJobs
     */
    void setScheduledJobs(List<JOB> scheduledJobs) ;

    /**
     * Get the job dependencies.
     *
     * @return
     */
    List<JobDependency> getJobDependencies();

    /**
     * Set the job dependencies.
     *
     * @param jobDependencies
     */
    void setJobDependencies(List<JobDependency> jobDependencies) ;

    /**
     * Get the contexts.
     *
     * @return
     */
    List<CONTEXT> getContexts();

    /**
     * Set the contexts.
     *
     * @param contexts
     */
    void setContexts(List<CONTEXT> contexts) ;

    /**
     * Get the context dependencies
     *
     * @return
     */
    List<ContextDependency> getContextDependencies();

    /**
     * Set the context dependencies
     *
     * @param contextDependencies
     */
    void setContextDependencies(List<ContextDependency> contextDependencies);

    /**
     * Get the scheduled job map
     *
     * @return
     */
    Map<String, JOB> getScheduledJobsMap();

    /**
     * Set the scheduled job map
     *
     * @return
     */
    Map<String, CONTEXT> getContextsMap();

    /**
     * Get the time window start cron expression
     * @return
     */
    String getTimeWindowStart();

    /**
     * Set the time window start cron expression
     *
     * @param timeWindowStart
     */
    void setTimeWindowStart(String timeWindowStart);

    /**
     * Get the ttl of the context in milliseconds since epoch.
     *
     * @return
     */
    long getContextTtlMilliseconds();

    /**
     * Set the ttl of the context in milliseconds since epoch.
     *
     * @param ttl
     */
    void setContextTtlMilliseconds(long ttl);

    /**
     * Set the job locks.
     *
     * @param jobLocks
     */
    void setJobLocks(List<JOB_LOCK> jobLocks);

    /**
     * Get the job locks.
     *
     * @return
     */
    List<JOB_LOCK> getJobLocks();

    /**
     * Get the jobs locks map.
     *
     * @return
     */
    Map<String, JOB_LOCK> getJobLocksMap();

    /**
     * Set the job lock map.
     *
     * @return
     */
    List<JOB_LOCK> getAllNestedJobLocks();

    /**
     * Get the environment group.
     * Group together Context that may require events sent between them.
     * @return
     */
    String getEnvironmentGroup();

    /**
     * Set the environment group.
     * @param environmentGroup
     */
    void setEnvironmentGroup(String environmentGroup);

    /**
     * Determine if all quartz schedule driven jobs are disabled for the context.
     *
     * @return
     */
    boolean isQuartzScheduleDrivenJobsDisabledForContext();

    /**
     * Set flag used to determine if all quartz schedule driven jobs are disabled for the context.
     *
     * @param isQuartzScheduleDrivenJobsDisabledForContext
     */
    void setQuartzScheduleDrivenJobsDisabledForContext(boolean isQuartzScheduleDrivenJobsDisabledForContext);

    /**
     * Determine whether display name should be displayed on entities within the job plan.
     *
     * @return
     */
    boolean isUseDisplayName();

    /**
     * Set flag used to determine if display name should be displayed on entities within the job plan.
     *
     * @param useDisplayName
     */
    void setUseDisplayName(boolean useDisplayName);

    /**
     * Get the tree view expand level. This assist with the UI tree view widget and the way it is presented.
     *
     * @return
     */
    int getTreeViewExpandLevel();

    /**
     * Set the tree view expand level.
     *
     * @param treeViewExpandLevel
     */
    void setTreeViewExpandLevel(int treeViewExpandLevel);

    /**
     * Get flag to indicate if concurrent versions of a context can run.
     *
     * @return
     */
    boolean isAbleToRunConcurrently();

    /**
     * Set flag to indicate if concurrent versions of a context can run.
     *
     * @param ableToRunConcurrently
     */
    void setAbleToRunConcurrently(boolean ableToRunConcurrently);


    /**
     * Sets the ordinal value for the Context object.
     * The ordinal value represents the order in which the Context is visualised.
     *
     * @param ordinal the ordinal value to set
     */
    void setOrdinal(int ordinal);


    /**
     * Returns the ordinal value of the Context object.
     * The ordinal value represents the order in which the Context is visualised.
     *
     * @return the ordinal value of the Context
     */
    int getOrdinal();

    /**
     * Sets the vertical spacing between job visualizations in the context.
     *
     * @param jobVisualisationVerticalSpacing The vertical spacing between job visualizations.
     */
    void setJobVisualisationVerticalSpacing(Integer  jobVisualisationVerticalSpacing);

    /**
     * Retrieves the vertical spacing for job visualizations.
     *
     * @return The vertical spacing for job visualizations.
     */
    Integer getJobVisualisationVerticalSpacing();

    /**
     * Sets the horizontal spacing for job visualisation in the context.
     *
     * @param jobVisualisationHorizontalSpacing the horizontal spacing value to set
     */
    void setJobVisualisationHorizontalSpacing(Integer jobVisualisationHorizontalSpacing);

    /**
     * Returns the horizontal spacing value for job visualisation.
     *
     * @return the horizontal spacing value
     */
    Integer getJobVisualisationHorizontalSpacing();

    /**
     * Sets the distance between visualisation levels for the context.
     *
     * @param contextVisualisationLevelDistance the distance between visualisation levels
     */
    void setContextVisualisationLevelDistance(Integer contextVisualisationLevelDistance);

    /**
     * Retrieves the visualisation level distance for the context.
     *
     * @return The visualisation level distance.
     */
    Integer getContextVisualisationLevelDistance();
    /**
     * Sets the distance between nodes in the visualisation of the context.
     *
     * @param contextVisualisationNodeDistance the distance between nodes in the visualisation
     */
    void setContextVisualisationNodeDistance(Integer contextVisualisationNodeDistance);

    /**
     * Retrieves the distance between nodes in the context visualization.
     * The distance between nodes determines the spacing between nodes in the visualization.
     * Note that this method does not return any result; it only sets the context visualisation node distance.
     */
    Integer getContextVisualisationNodeDistance();

    /**
     * Sets the font size for visualisation.
     *
     * @param fontSize The font size to set for visualisation.
     */
    void setVisualisationFontSize(Integer fontSize);

    /**
     * Retrieves the font size used in the visualization of the context.
     *
     * @return The font size used in the visualization.
     */
    Integer getVisualisationFontSize();
}
