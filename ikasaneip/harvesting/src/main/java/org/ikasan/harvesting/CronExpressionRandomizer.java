package org.ikasan.harvesting;

import java.util.Random;

/**
 * Utility class to randomize cron expressions using a job name as a seed.
 * This helps distribute scheduled job executions to avoid resource contention.
 *
 * Examples:
 * - "0 0/5 * * * ?" (every 5 minutes) -> randomizes to any second and minute within each 5-minute window
 * - "0/10 * * * * ?" (every 10 seconds) -> randomizes to any second within each 10-second window
 * - "0 0 0/1 * * ?" (every hour) -> randomizes to any second and minute within each hour
 *
 * @author Ikasan Development Team
 */
public class CronExpressionRandomizer {

    /**
     * Randomizes a cron expression based on the job name seed.
     *
     * @param cronExpression the original cron expression
     * @param seed the job name to use as a seed for randomization
     * @return the randomized cron expression
     */
    public static String randomize(String cronExpression, String seed) {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            throw new IllegalArgumentException("Cron expression cannot be null or empty");
        }
        if (seed == null || seed.trim().isEmpty()) {
            throw new IllegalArgumentException("Seed cannot be null or empty");
        }

        String[] parts = cronExpression.trim().split("\\s+");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid cron expression format: " + cronExpression);
        }

        Random random = new Random(seed.hashCode());

        String seconds = parts[0];
        String minutes = parts[1];
        String hours = parts[2];

        // Process seconds field
        String newSeconds = randomizeField(seconds, 60, random);

        // Process minutes field
        String newMinutes = randomizeField(minutes, 60, random);

        // Process hours field
        String newHours = randomizeField(hours, 24, random);

        // Reconstruct the cron expression
        StringBuilder result = new StringBuilder();
        result.append(newSeconds).append(" ");
        result.append(newMinutes).append(" ");
        result.append(newHours);

        // Append remaining fields unchanged
        for (int i = 3; i < parts.length; i++) {
            result.append(" ").append(parts[i]);
        }

        return result.toString();
    }

    /**
     * Randomizes a single cron field based on its pattern.
     *
     * @param field the field to randomize (e.g., "0", "0/5", "0-30/5", "*")
     * @param maxValue the maximum value for this field (60 for seconds/minutes, 24 for hours)
     * @param random the Random instance to use
     * @return the randomized field value
     */
    private static String randomizeField(String field, int maxValue, Random random) {
        if (field.equals("*")) {
            // If wildcard, keep it as wildcard
            return field;
        }

        if (field.equals("?")) {
            // Keep the no-specific-value marker
            return field;
        }

        if (field.contains("/")) {
            // Handle incremental patterns like "0/5" or "*/5"
            return randomizeIncrementalField(field, maxValue, random);
        }

        if (field.contains("-")) {
            // Handle range patterns like "0-30"
            return randomizeRangeField(field, maxValue, random);
        }

        if (field.matches("\\d+")) {
            // Single numeric value - randomize it within the max range
            return String.valueOf(random.nextInt(maxValue));
        }

        // For any other pattern, return as is
        return field;
    }

    /**
     * Randomizes an incremental field pattern like "0/5" or "0/10".
     * Returns a random value within the first interval of the increment.
     *
     * @param field the incremental field pattern
     * @param maxValue the maximum value for this field
     * @param random the Random instance to use
     * @return the randomized field value
     */
    private static String randomizeIncrementalField(String field, int maxValue, Random random) {
        String[] parts = field.split("/");
        int increment = Integer.parseInt(parts[1]);

        if (parts[0].equals("*") || parts[0].equals("0")) {
            // For patterns like "*/5" or "0/5", randomize within the increment
            int randomValue = random.nextInt(increment);
            return randomValue + "/" + increment;
        } else {
            // For patterns like "10/5", keep the base and increment
            int base = Integer.parseInt(parts[0]);
            int randomOffset = random.nextInt(increment);
            return (base + randomOffset) + "/" + increment;
        }
    }

    /**
     * Randomizes a range field pattern like "0-30" or "10-20".
     * Returns a random value within the range.
     *
     * @param field the range field pattern
     * @param maxValue the maximum value for this field
     * @param random the Random instance to use
     * @return the randomized field value
     */
    private static String randomizeRangeField(String field, int maxValue, Random random) {
        String[] parts = field.split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        int randomValue = start + random.nextInt(end - start + 1);
        return String.valueOf(randomValue);
    }
}
