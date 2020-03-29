package org.ikasan.solr.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolrTokenizerQueryBuilder {
    private static final String COLON = ":";
    private static final int WILDCARD = '*';
    private static final int QUOTE_CHARACTER = '\'';
    private static final int DOUBLE_QUOTE_CHARACTER = '"';

    private static List<String> LOGICAL_OPERATORS
        = Arrays.asList("&&", "||", "!", "(", ")", "AND", "OR", "NOT");

    public static String buildQuery(String queryString, String type, boolean negateQuery) throws IOException
    {
        List<String> tokens = tokenize(queryString);

        StringBuffer query = new StringBuffer();

        StringBuffer negationQuery = new StringBuffer();
        if(negateQuery)
        {
             negationQuery.append("*:* NOT ");
        }

        tokens.forEach(token ->
        {
            if(token.contains("*"))
            {
                query.append(negationQuery).append(type).append(COLON).append(SolrSpecialCharacterEscapeUtil.escape(token)).append(" ");
            }
            else if(LOGICAL_OPERATORS.contains(token))
            {
                query.append(SolrSpecialCharacterEscapeUtil.escape(token)).append(" ");
            }
            else
            {
                query.append(negationQuery).append(type).append(COLON).append("\"").append(token)
                    .append("\"").append(" ");
            }
        });

        return query.toString();
    }

    private static List<String> tokenize(String query) throws IOException
    {
        StreamTokenizer streamTokenizer = new StreamTokenizer(new StringReader(query));
        streamTokenizer.wordChars(':',':');
        streamTokenizer.wordChars('-','-');
        streamTokenizer.wordChars('+','+');
        List<String> tokens = new ArrayList<>();

        int currentToken = streamTokenizer.nextToken();
        while (currentToken != StreamTokenizer.TT_EOF)
        {
            if (streamTokenizer.ttype == StreamTokenizer.TT_NUMBER)
            {
                String token = String.format("%.0f", streamTokenizer.nval);
                currentToken = streamTokenizer.nextToken();
                if (streamTokenizer.ttype == WILDCARD)
                {
                    token += Character.toString((char)currentToken);
                    tokens.add(token);
                }
                else
                {
                    tokens.add(token);
                    continue;
                }
            } else if (streamTokenizer.ttype == StreamTokenizer.TT_WORD
                || streamTokenizer.ttype == QUOTE_CHARACTER
                || streamTokenizer.ttype == DOUBLE_QUOTE_CHARACTER)
            {
                String token = streamTokenizer.sval;
                currentToken = streamTokenizer.nextToken();
                if (streamTokenizer.ttype == WILDCARD)
                {
                    token += Character.toString((char)currentToken);
                    tokens.add(token);
                }
                else
                {
                    tokens.add(token);
                    continue;
                }
            }
            else if (streamTokenizer.ttype == WILDCARD)
            {
                String token = Character.toString((char)currentToken);
                currentToken = streamTokenizer.nextToken();
                if(currentToken != StreamTokenizer.TT_EOF)
                {
                    if(streamTokenizer.ttype == StreamTokenizer.TT_WORD || streamTokenizer.ttype == 34)
                    {
                        token += streamTokenizer.sval;
                    }
                    else if (streamTokenizer.ttype == StreamTokenizer.TT_NUMBER)
                    {
                        token += String.format("%.0f", streamTokenizer.nval);
                    }
                }
                else
                {
                    tokens.add(token);
                    continue;
                }
                currentToken = streamTokenizer.nextToken();
                if (currentToken != StreamTokenizer.TT_EOF)
                {
                    if(streamTokenizer.ttype == WILDCARD)
                    {
                        token += Character.toString((char)currentToken);
                        tokens.add(token);
                    }
                }
                else
                {
                    tokens.add(token);
                    continue;
                }
            }
            else
            {
                tokens.add(Character.toString((char)currentToken));
            }

            currentToken = streamTokenizer.nextToken();
        }

        return tokens;
    }
}
