package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorrelatingFileMessageProviderTest {

    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final JobExecutionContext mockContext = mockery.mock(JobExecutionContext.class);
    private final JobDataMap mockJobDataMap = mockery.mock(JobDataMap.class);
    private final CorrelatedFileConsumerConfiguration configuration = mockery.mock(CorrelatedFileConsumerConfiguration.class);
    private final ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    private static final List<String> FILENAME_PATTERNS_THAT_WILL_MATCH_THE_FS = Arrays.asList(
        "src/test/resources/data/unit/Trade_\\d{8}_\\d+_\\d{14}.txt",
        "src/test/resources/data/unit/TradeLeg_\\d{8}_\\d+_\\d{14}.txt");
    private static final List<String> FILENAME_PATTERNS_THAT_WILL_NOT_MATCH_THE_FS = Arrays.asList(
        "src/test/resources/data/unit/Trade_\\d{8}_\\d+_\\{14}.xxxtxt");

    @Test
    public void test_file_consumer_with_no_correlation_id_will_be_passive()
    {
        setupStandardFilenameExpectations();
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getDirectoryDepth();
                will(returnValue(1));

                exactly(1).of(mockContext).getMergedJobDataMap();
                will(returnValue(mockJobDataMap));
                exactly(1).of(mockJobDataMap).get(CorrelatingScheduledConsumer.CORRELATION_ID);
                will(returnValue(null));
            }
        });
        CorrelatedFileList files = messageProviderInvoke();
        Assert.assertNull(files);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_list_of_files()
    {
        // set test expectations
        setupCorrelationIdExpectations();
        setupStandardFilenameExpectations();
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
                exactly(2).of(configuration).getDirectoryDepth();
                will(returnValue(1));
            }
        });

        CorrelatedFileList files = messageProviderInvoke();
        Assert.assertTrue("Should have returned 2 files, but returned " + files.getFileList().size() + " files.", files.getFileList().size() == 2);
        Assert.assertEquals(files.toString(), "" +
            "CorrelatedFileList{fileList=[" +
            "./src/test/resources/data/unit/Trade_20141212_99_20141212121212.txt, " +
            "./src/test/resources/data/unit/TradeLeg_20141212_99_20141212121212.txt], " +
            "correlatingIdentifier='TestCorrelatingId'}");
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_unsuccessful_list_of_files()
    {
        // set test expectations
        setupCorrelationIdExpectations();
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilenames();
                will(returnValue(FILENAME_PATTERNS_THAT_WILL_NOT_MATCH_THE_FS));
                exactly(1).of(configuration).isDynamicFileName();
                will(returnValue(false));
                exactly(1).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(false));
                exactly(1).of(configuration).getDirectoryDepth();
                will(returnValue(1));
            }
        });

        CorrelatedFileList files = messageProviderInvoke();
        Assert.assertNull("Should have returned null", files);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_list_of_files_with_subdir()
    {
        // set test expectations
        setupCorrelationIdExpectations();
        setupStandardFilenameExpectations();
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
                exactly(2).of(configuration).getDirectoryDepth();
                will(returnValue(2));
            }
        });

        CorrelatedFileList files = messageProviderInvoke();

        Assert.assertTrue("Should have returned 3 files, but returned " + files.getFileList().size() + " files.", files.getFileList().size() == 3);
        Assert.assertTrue("Should have returned 3 files, but returned " + files.getFileList().size() + " files.", files.getFileList().size() == 3);
        Assert.assertTrue(files.getFileList().contains(new File("./src/test/resources/data/unit/Trade_20141212_99_20141212121212.txt")));
        Assert.assertTrue(files.getFileList().contains(new File("./src/test/resources/data/unit/subdir/Trade_20141212_99_20140000000000.txt")));
        Assert.assertTrue(files.getFileList().contains(new File("./src/test/resources/data/unit/TradeLeg_20141212_99_20141212121212.txt")));
        Assert.assertEquals("TestCorrelatingId", files.getCorrelatingIdentifier());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful return of an empty list of files.
     */
    @Test
    public void test_successful_empty_list_of_files()
    {
        // set test expectations
        setupCorrelationIdExpectations();
        mockery.checking(new Expectations() {
            {
                exactly(3).of(configuration).getFilenames();
                will(returnValue(new ArrayList<>()));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
            }
        });

        CorrelatedFileList files = messageProviderInvoke();
        Assert.assertNull("Should have returned null", files);

        mockery.assertIsSatisfied();
    }

    private CorrelatedFileList messageProviderInvoke() {
        CorrelatingFileMessageProvider messageProvider = new CorrelatingFileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        return messageProvider.invoke(mockContext);
    }

    private void setupCorrelationIdExpectations() {
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockContext).getMergedJobDataMap();
                will(returnValue(mockJobDataMap));
                exactly(1).of(mockJobDataMap).get(CorrelatingScheduledConsumer.CORRELATION_ID);
                will(returnValue("TestCorrelatingId"));
            }
        });
    }

    private void setupStandardFilenameExpectations() {
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilenames();
                will(returnValue(FILENAME_PATTERNS_THAT_WILL_MATCH_THE_FS));
                exactly(2).of(configuration).isDynamicFileName();
                will(returnValue(false));
                exactly(2).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
            }
        });
    }

}