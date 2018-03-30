package org.ikasan.solr.util;

/**
 * Created by stewmi on 30/03/2018.
 */
public class SolrSpecialCharacterEscapeUtil
{
    private static String[] TOKENS_TO_ESCAPE = {"\\", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[" , "]", "^", "\"", "~", "*", "?", ":"};

    public static String escape(String query)
    {
        for(String escapteToken: TOKENS_TO_ESCAPE)
        {
            query = query.replace(escapteToken, "\\" + escapteToken);
        }

        return query;
    }
}
