package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.component.broker.exception.MoveFileBrokerException;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MoveFileBrokerTest {

    // Each test gets its own throwaway TemporaryFolder directory, since each test is moving files
    // around, this makes it safe to run the tests concurrently should we decide to. The TempFolder
    // is automatically delete after the test.
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File sourceFile;
    private File archiveDir;

    @Before
    public void setUp() throws IOException {
        sourceFile = tempFolder.newFile("test.txt");
        archiveDir = new File(tempFolder.getRoot(), "archive");
        archiveDir.mkdirs();
    }

    @Test
    public void test_move_file_success() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_same_file_twice_move_file_success() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);

        FileUtils.copyFileToDirectory(new File(archiveDir, "test.txt"), tempFolder.getRoot(), true);

        broker.invoke(event);

        archiveFiles = archiveDir.list();

        Assert.assertEquals(2, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_success() {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setJobName("jobName");
        event.setDryRun(true);

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_job_dry_run_success() {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setJobName("jobName");
        event.setDryRun(true);

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_move_directory_same_as_src_directory() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(tempFolder.getRoot().getAbsolutePath());
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_move_directory_same_as_src_directory_due_to_dot() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(".");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_no_target_dir_success() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test(expected = MoveFileBrokerException.class)
    public void test_exception_bad_tgt_directory() {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("////\\\\\\////\\\\\\BAD DIRECTORY");
        event.setJobName("jobName");
        event.setDryRun(false);

        broker.invoke(event);
    }

    @Test
    public void test_move_file_with_spel_expression_success() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        String moveDirectoryPattern = new File(tempFolder.getRoot(), "{token}").getAbsolutePath();

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(moveDirectoryPattern);
        event.setMoveDirectorySpelExpression("#moveDirectoryPattern.replace('{token}', 'archive')");
        event.setJobName("jobName");

        broker.invoke(event);

        Assert.assertEquals(moveDirectoryPattern, event.getMoveDirectory());

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_move_file_with_spel_expression_using_correlating_identifier() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        String moveDirectoryPattern = new File(tempFolder.getRoot(), "{token}").getAbsolutePath();

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("archive");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(moveDirectoryPattern);
        event.setMoveDirectorySpelExpression("#moveDirectoryPattern.replace('{token}', #correlatingIdentifier)");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_move_file_with_conditional_spel_expression_success() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        String moveDirectoryPattern = new File(tempFolder.getRoot(), "{token}").getAbsolutePath();

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(moveDirectoryPattern);
        event.setMoveDirectorySpelExpression("#moveDirectoryPattern.contains('{token}') ? #moveDirectoryPattern.replace('{token}', 'archive') " +
            ": (#moveDirectoryPattern.contains('{other}') ? #moveDirectoryPattern.replace('{other}', 'archive') : #moveDirectoryPattern)");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_move_file_with_conditional_spel_expression_no_replacement() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setMoveDirectorySpelExpression("#moveDirectoryPattern.contains('{token}') ? #moveDirectoryPattern.replace('{token}', 'archive') " +
            ": (#moveDirectoryPattern.contains('{other}') ? #moveDirectoryPattern.replace('{other}', 'archive') : #moveDirectoryPattern)");
        event.setJobName("jobName");

        broker.invoke(event);

        Assert.assertEquals(archiveDir.getAbsolutePath(), event.getMoveDirectory());

        String[] archiveFiles = archiveDir.list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_move_file_with_spel_expression_multiple_invocations_with_changed_correlating_identifier() throws IOException {
        List<File> files = List.of(sourceFile);

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("first");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(new File(archiveDir, "{token}").getAbsolutePath());
        event.setMoveDirectorySpelExpression("#moveDirectoryPattern.replace('{token}', #correlatingIdentifier)");
        event.setJobName("jobName");

        broker.invoke(event);

        File firstArchiveDir = new File(archiveDir, "first");
        Assert.assertTrue(firstArchiveDir.exists());
        Assert.assertEquals("test.txt", firstArchiveDir.list()[0]);

        FileUtils.copyFileToDirectory(new File(firstArchiveDir, "test.txt"), tempFolder.getRoot(), true);

        event.setCorrelationIdentifier("second");

        broker.invoke(event);

        File secondArchiveDir = new File(archiveDir, "second");
        Assert.assertTrue(secondArchiveDir.exists());
        Assert.assertEquals("test.txt", secondArchiveDir.list()[0]);

        // first invocation's file untouched by the second invocation, proving the SpEL was re-resolved per-call rather than cached
        Assert.assertEquals("test.txt", firstArchiveDir.list()[0]);
    }

    @Test(expected = MoveFileBrokerException.class)
    public void test_exception_bad_src_file() throws IOException {
        List<File> files = List.of(new File("///\\\\////\\\\BAD FILE PATH/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory(archiveDir.getAbsolutePath());
        event.setJobName("jobName");

        broker.invoke(event);
        // MoveFileBrokerException thown by above call so no further assertions.
    }
}