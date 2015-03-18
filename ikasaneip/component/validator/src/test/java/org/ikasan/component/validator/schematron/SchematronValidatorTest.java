package org.ikasan.component.validator.schematron;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.log.Log;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.component.validator.ValidationResult;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the SchematronValidator
 */
public class SchematronValidatorTest
{
    private static Server server;
    private static String baseUrl;

    @BeforeClass
    public static void startJetty() throws Exception
    {
        // prevent logging
        Log.setLog(null);
        server = new Server(0);
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(0);
        server.addConnector(connector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(".");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        int localPort = server.getConnectors()[0].getLocalPort();
        baseUrl = "http://localhost:" + localPort + "/src/test/resources/";
    }

    @AfterClass
    public static void stopJetty() throws Exception
    {
        server.stop();
    }

    @Test
    public void test_valid_no_namespace()
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri("classpath:xslt/basic-no-namespace.sch.xsl");
        validator.setConfiguration(configuration);
        validator.setUriResolver(new ClasspathUriResolver());
        validator.startManagedResource();

        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/simple-no-namespace.xml"));

        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }

    @Test
    public void test_valid_with_namespace()
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri("classpath:xslt/basic-with-namespace.sch.xsl");
        validator.setConfiguration(configuration);
        validator.setUriResolver(new ClasspathUriResolver());
        validator.startManagedResource();

        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/simple-with-namespace.xml"));

        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }

    @Test
    public void test_invalid_no_namespace()
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri("classpath:xslt/basic-no-namespace.sch.xsl");
        validator.setConfiguration(configuration);
        validator.setUriResolver(new ClasspathUriResolver());
        validator.startManagedResource();

        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/invalid-no-namespace.xml"));

        Assert.assertTrue("Document should be invalid", validationResult.getResult().equals(ValidationResult.Result.INVALID));
    }


    @Test
    public void test_valid_over_http() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/basic-no-namespace.sch.xsl");
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/simple-no-namespace.xml"));

        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }

    @Test
    public void test_valid_over_http_relative() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-import.sch.xsl");
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/simple-no-namespace.xml"));

        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }


    @Test
    public void test_valid_over_http_relative_codelist() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-codelist.sch.xsl");
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/passes-codelist.xml"));
        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }

    @Test
    public void test_invalid_over_http_relative_codelist() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-codelist.sch.xsl");
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/fails-codelist.xml"));
        Assert.assertTrue("Document should be invalid", validationResult.getResult().equals(ValidationResult.Result.INVALID));
    }

    @Test
    public void test_invalid_over_http_with_skip() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-codelist.sch.xsl");
        configuration.setSkipValidation(true);
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/fails-codelist.xml"));
        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
    }

    @Test
    public void test_valid_over_http_with_ignore() throws Exception
    {
        final String ruleToIgnore = "code-list-check-value";
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-codelist.sch.xsl");
        List<String> rulesToIgnore = new ArrayList<>();
        rulesToIgnore.add(ruleToIgnore);
        configuration.setRulesToIgnore(rulesToIgnore);
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/fails-codelist.xml"));
        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
        Assert.assertTrue("Should have ignored: " + ruleToIgnore, validationResult.getIgnoredRules().contains(ruleToIgnore));
    }

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Test
    public void test_missing_resource_over_http() throws Exception
    {
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(TransformerConfigurationException.class));
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/missing.sch.xsl");
        validator.setConfiguration(configuration);
        validator.startManagedResource();
    }

    // this fails since the codelist Source is not cached by the resolver, but is by the transformer - reuse transformer?
    @Test
    public void test_valid_over_http_relative_codelist_stopped_webserver() throws Exception
    {
        SchematronValidator validator = new SchematronValidator();
        SchematronValidatorConfiguration configuration = new SchematronValidatorConfiguration();
        configuration.setSchematronUri(baseUrl + "xslt/dir1/relative-codelist.sch.xsl");
        validator.setConfiguration(configuration);
        validator.setUriResolver(new ClasspathUriResolver());
        validator.startManagedResource();
        ValidationResult<Document, Document> validationResult = validator.invoke(getDocument("xml/passes-codelist.xml"));
        Assert.assertTrue("Document should be valid", validationResult.getResult().equals(ValidationResult.Result.VALID));
        // stop the webserver
        stopJetty();
        ValidationResult<Document, Document> validationResult2 = validator.invoke(getDocument("xml/passes-codelist.xml"));
        Assert.assertTrue("Document should still be valid", validationResult2.getResult().equals(ValidationResult.Result.VALID));
        // start it back up
        startJetty();
    }

    private Document getDocument(String resource)
    {
        try
        {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ClassLoader.getSystemResourceAsStream(resource));
        }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
    }


}
