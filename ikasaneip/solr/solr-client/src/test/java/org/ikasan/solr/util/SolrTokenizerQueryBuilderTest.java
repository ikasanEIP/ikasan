package org.ikasan.solr.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SolrTokenizerQueryBuilderTest {

    @Test
    public void test_logical_grouping() throws IOException {
        String query = "\"name pair\" AND (dog OR cat)";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:\"name pair\" AND ( payload:\"dog\" OR payload:\"cat\" ) ", solrQuery);
    }

    @Test
    public void test_logical_nested_grouping() throws IOException {
        String query = "\"name pair\" AND ((dog AND horse) OR (cat AND chicken))";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:\"name pair\" AND ( ( payload:\"dog\" AND payload:\"horse\" ) OR ( payload:\"cat\" AND payload:\"chicken\" ) ) ", solrQuery);
    }

    @Test
    public void test_with_wildcard_start() throws IOException {
        String query = "*12345";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:*12345 ", solrQuery);
    }

    @Test
    public void test_with_wildcard_end() throws IOException {
        String query = "12345*";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:12345* ", solrQuery);
    }

    @Test
    public void test_with_wildcard_start_and_end() throws IOException {
        String query = "*12345*";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:*12345* ", solrQuery);
    }

    @Test
    public void test_with_query_with_token_seperators() throws IOException {
        String query = "3345:4432-bb:9";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:\"3345\" payload:\":4432-bb:9\" ", solrQuery);
    }

    @Test
    public void test_with_query_with_wildcards_and_with_token_seperators() throws IOException {
        String query = "*\":4432-bb:\"*";

        String solrQuery = SolrTokenizerQueryBuilder.buildQuery(query, "payload");

        Assert.assertEquals("Query equals", "payload:*\\:4432\\-bb\\:* ", solrQuery);
    }
}
