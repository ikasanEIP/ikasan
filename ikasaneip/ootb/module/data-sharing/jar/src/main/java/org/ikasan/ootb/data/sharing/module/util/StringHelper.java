package org.ikasan.ootb.data.sharing.module.util;

import java.util.List;

public class StringHelper {

    /**
     * Converts a comma-separated string into a list of trimmed strings.
     * Empty strings resulting from consecutive commas or leading/trailing commas
     * are excluded from the resulting list.
     *
     * @param commaSeparatedString the input string containing comma-separated values.
     *                             If null or empty, an empty list is returned.
     * @return a list of non-empty, trimmed strings derived from the input string,
     *         or an empty list if the input is null or blank.
     */
    public static List<String> commaSeparatedStringToList(String commaSeparatedString) {
        if (commaSeparatedString == null || commaSeparatedString.trim().isEmpty()) {
            return List.of();
        }
        return List.of(commaSeparatedString.split(","))
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
}
