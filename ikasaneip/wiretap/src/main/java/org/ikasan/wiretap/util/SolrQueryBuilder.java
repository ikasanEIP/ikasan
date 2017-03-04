package org.ikasan.wiretap.util;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public abstract class SolrQueryBuilder
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrQueryBuilder.class);

    public static final String AND = "AND";
    public static final String OR = "OR ";

    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";

    /**
     * Helper method to create a composite query.
     *
     * @param values the values what the query will be against.
     * @param queryFieldName the field that is being queried.
     * @param operator the operator on the query.
     *
     * @return
     */
    protected StringBuffer createCompositeQuery(Collection<String> values, String queryFieldName, String operator)
    {
        StringBuffer queryBuffer = new StringBuffer();
        String delim = "";

        if(values != null && values.size() > 0)
        {
            queryBuffer.append(queryFieldName);

            queryBuffer.append(OPEN_BRACKET);

            for (String value : values)
            {
                queryBuffer.append(delim).append("\"").append(value).append("\" ");
                delim = operator;
            }

            queryBuffer.append(CLOSE_BRACKET);
        }

        return queryBuffer;
    }

    /**
     * Helper method to create a date between query.
     *
     * @param from
     * @param to
     * @param dateFieldName
     *
     * @return
     */
    protected StringBuffer createDateBetweenQuery(Date from, Date to, String dateFieldName)
    {
        StringBuffer dateBuffer = new StringBuffer();

        if(from == null && to == null)
        {
            dateBuffer.append(dateFieldName).append("[").append("* TO *").append("]");
        }
        else if(from == null)
        {
            dateBuffer.append(dateFieldName).append("[").append("* TO ").append(to.getTime()).append("]");
        }
        else if(to == null)
        {
            dateBuffer.append(dateFieldName).append("[").append(from.getTime()).append(" TO *").append("]");
        }
        else
        {
            dateBuffer.append(dateFieldName).append("[").append(from.getTime()).append(" TO ").append(to.getTime()).append("]");
        }

        return dateBuffer;
    }

    /**
     * Helper method to build the final query.
     *
     * @param queryBuffer
     * @param hasPrevious
     *
     * @return
     */
    protected StringBuffer buildFinalQuery(StringBuffer queryBuffer, boolean hasPrevious)
    {
        StringBuffer bufferFinalQuery = new StringBuffer();

        if(queryBuffer != null && !queryBuffer.toString().isEmpty())
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(" ").append(AND).append(" ");
            }

            bufferFinalQuery.append(queryBuffer);
        }

        return bufferFinalQuery;
    }

    protected StringBuffer createOpenQuery()
    {
        StringBuffer dateBuffer = new StringBuffer();

        dateBuffer.append("*:*");

        return dateBuffer;
    }
}
