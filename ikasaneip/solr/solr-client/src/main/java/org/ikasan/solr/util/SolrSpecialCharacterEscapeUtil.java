package org.ikasan.solr.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by stewmi on 30/03/2018.
 */
public class SolrSpecialCharacterEscapeUtil {
    private static String[] LOGICAL_OPERATORS = {"&&", "||", "!", "(", ")", "AND", "OR"};
    private static String[] TOKENS_TO_ESCAPE = { "+", "-", ":"};

    public static String escape(String query) {
        for(String escapeToken: TOKENS_TO_ESCAPE) {
            query = query.replace(escapeToken, "\\" + escapeToken);
        }

        return query;
    }

    public static boolean containsSpecialChar(String query) {
         return Arrays.stream(TOKENS_TO_ESCAPE).filter(token -> query.contains(token))
             .collect(Collectors.toList()).size() > 0;
    }

    public static boolean containsLogicalOperators(String query) {
        return Arrays.stream(LOGICAL_OPERATORS).filter(token -> query.contains(token))
            .collect(Collectors.toList()).size() > 0;
    }
}
