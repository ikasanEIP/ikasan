package org.ikasan.harvesting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for CronExpressionRandomizer.
 *
 * @author Ikasan Development Team
 */
public class CronExpressionRandomizerTest {

    @Test
    public void testRandomizeEvery5Minutes() {
        String cronExpression = "0 0/5 * * * ?";
        String jobName = "testJob1";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        // Verify the format
        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify seconds is randomized
        String seconds = parts[0];
        Assert.assertTrue("Seconds should be numeric or pattern", seconds.matches("\\d+(/\\d+)?"));

        // Verify minutes has the /5 increment
        String minutes = parts[1];
        Assert.assertTrue("Minutes should contain /5 pattern", minutes.contains("/5"));

        // Verify hours remains wildcard
        String hours = parts[2];
        Assert.assertEquals("Hours should remain wildcard", "*", hours);

        // Verify remaining parts are unchanged
        Assert.assertEquals("*", parts[3]);
        Assert.assertEquals("*", parts[4]);
        Assert.assertEquals("?", parts[5]);
    }

    @Test
    public void testRandomizeEvery10Seconds() {
        String cronExpression = "0/10 * * * * ?";
        String jobName = "testJob2";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        // Verify the format
        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify seconds has the /10 increment
        String seconds = parts[0];
        Assert.assertTrue("Seconds should contain /10 pattern", seconds.contains("/10"));

        // Extract the base value
        String[] secondsParts = seconds.split("/");
        int baseSeconds = Integer.parseInt(secondsParts[0]);
        int increment = Integer.parseInt(secondsParts[1]);

        Assert.assertTrue("Base seconds should be within increment", baseSeconds < increment);
        Assert.assertEquals("Increment should be 10", 10, increment);
    }

    @Test
    public void testRandomizeEveryHour() {
        String cronExpression = "0 0 0/1 * * ?";
        String jobName = "testJob3";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        // Verify the format
        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify seconds is randomized
        String seconds = parts[0];
        Assert.assertTrue("Seconds should be numeric", seconds.matches("\\d+"));
        int secondsValue = Integer.parseInt(seconds);
        Assert.assertTrue("Seconds should be 0-59", secondsValue >= 0 && secondsValue < 60);

        // Verify minutes is randomized
        String minutes = parts[1];
        Assert.assertTrue("Minutes should be numeric", minutes.matches("\\d+"));
        int minutesValue = Integer.parseInt(minutes);
        Assert.assertTrue("Minutes should be 0-59", minutesValue >= 0 && minutesValue < 60);

        // Verify hours has the /1 increment
        String hours = parts[2];
        Assert.assertTrue("Hours should contain /1 pattern", hours.contains("/1"));
    }

    @Test
    public void testDeterministicRandomization() {
        String cronExpression = "0 0/5 * * * ?";
        String jobName = "consistentJob";

        String result1 = CronExpressionRandomizer.randomize(cronExpression, jobName);
        String result2 = CronExpressionRandomizer.randomize(cronExpression, jobName);

        Assert.assertEquals("Same job name should produce same randomized cron", result1, result2);
    }

    @Test
    public void testDifferentJobsProduceDifferentResults() {
        String cronExpression = "0 0/5 * * * ?";
        String jobName1 = "job1";
        String jobName2 = "job2";

        String result1 = CronExpressionRandomizer.randomize(cronExpression, jobName1);
        String result2 = CronExpressionRandomizer.randomize(cronExpression, jobName2);

        // While it's theoretically possible they could be the same, it's highly unlikely
        Assert.assertNotEquals("Different job names should typically produce different results", result1, result2);
    }

    @Test
    public void testRandomizeWithWildcards() {
        String cronExpression = "* * * * * ?";
        String jobName = "wildcardJob";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify wildcards remain as wildcards
        Assert.assertEquals("Seconds should remain wildcard", "*", parts[0]);
        Assert.assertEquals("Minutes should remain wildcard", "*", parts[1]);
        Assert.assertEquals("Hours should remain wildcard", "*", parts[2]);
        Assert.assertEquals("Day of month should remain wildcard", "*", parts[3]);
        Assert.assertEquals("Month should remain wildcard", "*", parts[4]);
        Assert.assertEquals("Day of week should remain ?", "?", parts[5]);
    }

    @Test
    public void testRandomizeWithSpecificValues() {
        String cronExpression = "0 15 10 * * ?";
        String jobName = "specificJob";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Specific numeric values should be randomized
        Assert.assertTrue("Seconds should be numeric", parts[0].matches("\\d+"));
        Assert.assertTrue("Minutes should be numeric", parts[1].matches("\\d+"));
        Assert.assertTrue("Hours should be numeric", parts[2].matches("\\d+"));

        // Wildcards should remain unchanged
        Assert.assertEquals("*", parts[3]);
        Assert.assertEquals("*", parts[4]);
        Assert.assertEquals("?", parts[5]);
    }

    @Test
    public void testRandomizeEvery2Hours() {
        String cronExpression = "0 0 0/2 * * ?";
        String jobName = "every2hours";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify hours has the /2 increment
        String hours = parts[2];
        Assert.assertTrue("Hours should contain /2 pattern", hours.contains("/2"));

        // Extract the base value
        String[] hoursParts = hours.split("/");
        int baseHours = Integer.parseInt(hoursParts[0]);
        int increment = Integer.parseInt(hoursParts[1]);

        Assert.assertTrue("Base hours should be within increment", baseHours < increment);
        Assert.assertEquals("Increment should be 2", 2, increment);
    }

    @Test
    public void testRandomizeWithRange() {
        String cronExpression = "0 0-30 * * * ?";
        String jobName = "rangeJob";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Minutes should be randomized within the range
        String minutes = parts[1];
        Assert.assertTrue("Minutes should be numeric", minutes.matches("\\d+"));
        int minutesValue = Integer.parseInt(minutes);
        Assert.assertTrue("Minutes should be within range 0-30", minutesValue >= 0 && minutesValue <= 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCronExpression() {
        CronExpressionRandomizer.randomize(null, "jobName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCronExpression() {
        CronExpressionRandomizer.randomize("", "jobName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullJobName() {
        CronExpressionRandomizer.randomize("0 0/5 * * * ?", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyJobName() {
        CronExpressionRandomizer.randomize("0 0/5 * * * ?", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCronExpression() {
        CronExpressionRandomizer.randomize("0 0/5", "jobName");
    }

    @Test
    public void testRandomizeEvery30Seconds() {
        String cronExpression = "0/30 * * * * ?";
        String jobName = "every30seconds";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify seconds has the /30 increment
        String seconds = parts[0];
        Assert.assertTrue("Seconds should contain /30 pattern", seconds.contains("/30"));

        // Extract and validate the base value
        String[] secondsParts = seconds.split("/");
        int baseSeconds = Integer.parseInt(secondsParts[0]);
        Assert.assertTrue("Base seconds should be 0-29", baseSeconds >= 0 && baseSeconds < 30);
    }

    @Test
    public void testRandomizeEvery15Minutes() {
        String cronExpression = "0 0/15 * * * ?";
        String jobName = "every15minutes";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 6 parts", 6, parts.length);

        // Verify minutes has the /15 increment
        String minutes = parts[1];
        Assert.assertTrue("Minutes should contain /15 pattern", minutes.contains("/15"));

        // Extract and validate the base value
        String[] minutesParts = minutes.split("/");
        int baseMinutes = Integer.parseInt(minutesParts[0]);
        Assert.assertTrue("Base minutes should be 0-14", baseMinutes >= 0 && baseMinutes < 15);
    }

    @Test
    public void testPreservesSevenFieldCronExpression() {
        String cronExpression = "0 0/5 * * * ? 2024";
        String jobName = "sevenFieldJob";

        String result = CronExpressionRandomizer.randomize(cronExpression, jobName);

        String[] parts = result.split("\\s+");
        Assert.assertEquals("Should have 7 parts", 7, parts.length);

        // Verify the year field is preserved
        Assert.assertEquals("Year should be preserved", "2024", parts[6]);
    }
}
