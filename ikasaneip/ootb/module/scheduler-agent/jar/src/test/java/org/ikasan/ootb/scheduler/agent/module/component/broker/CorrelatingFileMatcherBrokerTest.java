package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.ikasan.component.endpoint.filesystem.messageprovider.DynamicFileMatcher;
import org.ikasan.ootb.scheduler.agent.module.component.broker.exception.CorrelatingFileMatcherBrokerException;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class CorrelatingFileMatcherBrokerTest {
    private CorrelatingFileMatcherBroker broker;
    @Mock
    private DynamicFileMatcher fileMatcher;
    @Mock
    private FileWatcherJobEvent fileWatcherJobEvent;

    @Mock
    private List<String> filenames;

    @Before
    public void setup() {
        fileMatcher = mock(DynamicFileMatcher.class);
        fileWatcherJobEvent = mock(FileWatcherJobEvent.class);
        filenames = mock(List.class);
        broker = new CorrelatingFileMatcherBroker() {
            @Override
            protected DynamicFileMatcher getFileMatcher(String filePath, String filename, boolean dynamicFileName,
                                                        boolean ignoreFileNameWhistScanning, int directoryDepth,
                                                        String filenameSpelExpression, String filePathSpelExpression,
                                                        boolean followSymbolicLinks) {
                return fileMatcher;
            }

            @Override
            protected List<String> getFilenames() {
                return filenames;
            }
        };
    }

    @Test
    public void test_invoke_no_file_match() throws EndpointException, IOException {
        this.filenames = new ArrayList<>();
        String correlationIdentifier = "correlationIdentifier";
        String filePath = "filePath";
        String fileName = "fileName";
        String fileNameSpelExpression = "fileNameSpelExpression";
        String filePathSpelExpression = "filePathSpelExpression";

        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.getFilePath()).thenReturn(filePath);
        when(fileWatcherJobEvent.getFilename()).thenReturn(fileName);
        when(fileWatcherJobEvent.getFileNameSpelExpression()).thenReturn(fileNameSpelExpression);
        when(fileWatcherJobEvent.getFilePathSpelExpression()).thenReturn(filePathSpelExpression);

        broker.startManagedResource();

        FileWatcherJobEvent result = broker.invoke(fileWatcherJobEvent);

        verify(fileMatcher).setCorrelatingIdentifier(correlationIdentifier);
        verify(fileMatcher).invoke();

        Assert.assertNull(result);
    }

    @Test
    public void test_invoke_files_matched() throws EndpointException, IOException {
        String correlationIdentifier = "correlationIdentifier";
        String filePath = "filePath";
        String fileName = "fileName";
        String fileNameSpelExpression = "fileNameSpelExpression";
        String filePathSpelExpression = "filePathSpelExpression";

        List<String> files = new ArrayList<>();
        files.add("file1.txt");
        files.add("file2.txt");

        when(this.filenames.iterator()).thenReturn(files.iterator());
        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.getFilePath()).thenReturn(filePath);
        when(fileWatcherJobEvent.getFilename()).thenReturn(fileName);
        when(fileWatcherJobEvent.isDryRun()).thenReturn(false);
        when(fileWatcherJobEvent.getFileNameSpelExpression()).thenReturn(fileNameSpelExpression);
        when(fileWatcherJobEvent.getFilePathSpelExpression()).thenReturn(filePathSpelExpression);

        broker.startManagedResource();

        FileWatcherJobEvent result = broker.invoke(fileWatcherJobEvent);

        verify(fileMatcher).setCorrelatingIdentifier(correlationIdentifier);
        verify(fileMatcher).invoke();
        verify(fileWatcherJobEvent).setCorrelatedFileList(any());
        verify(fileWatcherJobEvent).getCorrelationIdentifier();
        verify(fileWatcherJobEvent).getFilePath();
        verify(fileWatcherJobEvent).getFilename();
        verify(fileWatcherJobEvent).getFileNameSpelExpression();
        verify(fileWatcherJobEvent).getFilePathSpelExpression();
        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);

        Assert.assertNotNull(result);
    }

    @Test
    public void test_invoke_dry_run() throws EndpointException, IOException {
        when(fileWatcherJobEvent.isDryRun()).thenReturn(true);

        broker.startManagedResource();

        FileWatcherJobEvent result = broker.invoke(fileWatcherJobEvent);

        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);

        Assert.assertNotNull(result);
    }

    @Test
    public void test_invoke_null_correlation_identifier_file_match() throws EndpointException, IOException {
        String correlationIdentifier = null;
        String filePath = "filePath";
        String fileName = "fileName";
        String fileNameSpelExpression = "fileNameSpelExpression";
        String filePathSpelExpression = "filePathSpelExpression";

        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.getFilePath()).thenReturn(filePath);
        when(fileWatcherJobEvent.getFilename()).thenReturn(fileName);
        when(fileWatcherJobEvent.isDryRun()).thenReturn(false);
        when(fileWatcherJobEvent.getFileNameSpelExpression()).thenReturn(fileNameSpelExpression);
        when(fileWatcherJobEvent.getFilePathSpelExpression()).thenReturn(filePathSpelExpression);

        FileWatcherJobEvent result = broker.invoke(fileWatcherJobEvent);

        verify(fileWatcherJobEvent).getCorrelationIdentifier();
        verify(fileWatcherJobEvent).getFilePath();
        verify(fileWatcherJobEvent).getFilename();
        verify(fileWatcherJobEvent).getFileNameSpelExpression();
        verify(fileWatcherJobEvent).getFilePathSpelExpression();
        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);

        Assert.assertNull(result);
    }

    @Test
    public void test_invoke_with_IOException_throws_endpoint_exception() throws IOException {
        this.filenames = new ArrayList<>();
        doThrow(new IOException()).when(fileMatcher).invoke();

        String correlationIdentifier = "correlationIdentifier";

        broker.startManagedResource();

        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.isDryRun()).thenReturn(false);

        Assert.assertThrows(CorrelatingFileMatcherBrokerException.class, () -> broker.invoke(fileWatcherJobEvent));

        verify(fileMatcher).setCorrelatingIdentifier(correlationIdentifier);
        verify(fileMatcher).invoke();
        verify(fileWatcherJobEvent).getCorrelationIdentifier();
        verify(fileWatcherJobEvent).getFilePath();
        verify(fileWatcherJobEvent).getFilename();
        verify(fileWatcherJobEvent).getFileNameSpelExpression();
        verify(fileWatcherJobEvent).getFilePathSpelExpression();
        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);
    }

    @Test
    public void test_invoke_with_ConcurrentModificationException_throws_endpoint_exception() throws IOException {
        this.filenames = new ArrayList<>();
        doThrow(new ConcurrentModificationException()).when(fileMatcher).invoke();

        String correlationIdentifier = "correlationIdentifier";

        broker.startManagedResource();

        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.isDryRun()).thenReturn(false);

        Assert.assertThrows(ConcurrentModificationException.class, () -> broker.invoke(fileWatcherJobEvent));

        verify(fileMatcher).setCorrelatingIdentifier(correlationIdentifier);
        verify(fileMatcher).invoke();
        verify(fileWatcherJobEvent).getCorrelationIdentifier();
        verify(fileWatcherJobEvent).getFilePath();
        verify(fileWatcherJobEvent).getFilename();
        verify(fileWatcherJobEvent).getFileNameSpelExpression();
        verify(fileWatcherJobEvent).getFilePathSpelExpression();
        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);
    }

    @Test
    public void test_invoke_with_ConcurrentModificationException_throws_ForceTransactionRollbackException() throws IOException {
        this.filenames = new ArrayList<>();
        doThrow(new ConcurrentModificationException()).when(fileMatcher).invoke();

        String correlationIdentifier = "correlationIdentifier";


        when(fileWatcherJobEvent.getCorrelationIdentifier()).thenReturn(correlationIdentifier);
        when(fileWatcherJobEvent.isDryRun()).thenReturn(false);

        Assert.assertThrows(ForceTransactionRollbackException.class, () -> broker.invoke(fileWatcherJobEvent));

        verify(fileMatcher).setCorrelatingIdentifier(correlationIdentifier);
        verify(fileMatcher).invoke();
        verify(fileWatcherJobEvent).getCorrelationIdentifier();
        verify(fileWatcherJobEvent).getFilePath();
        verify(fileWatcherJobEvent).getFilename();
        verify(fileWatcherJobEvent).getFileNameSpelExpression();
        verify(fileWatcherJobEvent).getFilePathSpelExpression();
        verify(fileWatcherJobEvent).isDryRun();

        verifyNoMoreInteractions(fileWatcherJobEvent
            , fileMatcher);
    }
}
