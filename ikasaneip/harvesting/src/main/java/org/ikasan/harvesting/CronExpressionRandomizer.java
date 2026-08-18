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
     * If a specific time is set (all time fields are fixed numeric values), the expression is returned unchanged.
     *
     * @param cronExpression the original cron expression
     * @param seed the job name to use as a seed for randomization
     * @return the randomized cron expression, or the original if a specific time is set
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

        String seconds = parts[0];
        String minutes = parts[1];
        String hours = parts[2];

        // Check if this is a specific time (all three time fields are fixed numeric values)
        if (isSpecificTime(seconds, minutes, hours)) {
            // Don't randomize specific times - return as is
            return cronExpression;
        }

        Random random = new Random(seed.hashCode());

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
     * Checks if the time fields represent a specific time (all are fixed numeric values).
     *
     * @param seconds the seconds field
     * @param minutes the minutes field
     * @param hours the hours field
     * @return true if all three fields are fixed numeric values
     */
    private static boolean isSpecificTime(String seconds, String minutes, String hours) {
        return isFixedNumericValue(seconds) && isFixedNumericValue(minutes) && isFixedNumericValue(hours);
    }

    /**
     * Checks if a field is a fixed numeric value (not a wildcard, range, or increment).
     *
     * @param field the field to check
     * @return true if the field is a simple numeric value
     */
    private static boolean isFixedNumericValue(String field) {
        return field.matches("\\d+");
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
            // Single numeric value - keep as is (specific time should not be randomized)
            return field;
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
