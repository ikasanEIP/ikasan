package org.ikasan.solr.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by stewmi on 30/03/2018.
 */
public class SolrSpecialCharacterEscapeUtilTest
{
    @Test
    public void test()
    {
        String result = SolrSpecialCharacterEscapeUtil.escape("(1+1):2");

        Assert.assertEquals("Escaped string must equal!", "\\(1\\+1\\)\\:2", result);
    }
}
