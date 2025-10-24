package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MoveFileBrokerTest {

    @Before
    public void createArchiveDirectory() throws IOException {
        File archiveDirectory = new File("src/test/resources/data/archive");
        if (!archiveDirectory.exists()) {
            archiveDirectory.mkdirs();
        }

        if(new File("src/test/resources/data/archive/test.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
                , new File("src/test/resources/data"), true);
        }
        FileUtils.cleanDirectory(new File("src/test/resources/data/archive"));
    }

    @Test
    public void test_move_file_success() throws IOException {
       List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/archive");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals("test.txt", archiveFiles[0]);
    }

    @Test
    public void test_same_file_twice_move_file_success() throws IOException {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/archive");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals("test.txt", archiveFiles[0]);

        FileUtils.copyFileToDirectory(new File("src/test/resources/data/archive/test.txt")
            , new File("src/test/resources/data"), true);

        broker.invoke(event);

        archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(2, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_success() {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/archive");
        event.setJobName("jobName");
        event.setDryRun(true);

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_job_dry_run_success() {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/archive");
        event.setJobName("jobName");
        event.setDryRun(true);

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_exception_bad_move_directory_same_as_src_directory() throws IOException {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setMoveDirectory("src/test/resources/data/");
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_file_dry_run_exception_bad_move_directory_same_as_src_directory_due_to_dot() throws IOException {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

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

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test
    public void test_move_no_target_dir_success() throws IOException {
        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBroker broker = new MoveFileBroker();

        CorrelatedFileList correlatedFileList = new CorrelatedFileList(files, "correlationIdentifier");

        FileWatcherJobEvent event = new FileWatcherJobEvent();
        event.setCorrelationIdentifier("correlationIdentifier");
        event.setCorrelatedFileList(correlatedFileList);
        event.setJobName("jobName");
        event.setMinFileAgeSeconds(30);
        event.setJobName("jobName");

        broker.invoke(event);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }
}
