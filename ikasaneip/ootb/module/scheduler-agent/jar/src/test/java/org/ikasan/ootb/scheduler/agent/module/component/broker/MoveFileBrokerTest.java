package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MoveFileBrokerTest {

    @Mock
    DryRunModeService dryRunModeService;

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_dry_run_service() {
        new MoveFileBroker(null);
    }

    @Test
    public void test_move_file_success() throws IOException {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);

        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBrokerConfiguration configuration = new MoveFileBrokerConfiguration();
        configuration.setMoveDirectory("src/test/resources/data/archive");
        MoveFileBroker broker = new MoveFileBroker(this.dryRunModeService);
        broker.setConfiguration(configuration);

        broker.invoke(files);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals("test.txt", archiveFiles[0]);

        FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
            , new File("src/test/resources/data"), true);
    }

    @Test
    public void test_move_file_dry_run_success() throws IOException {
        when(dryRunModeService.getDryRunMode()).thenReturn(true);

        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBrokerConfiguration configuration = new MoveFileBrokerConfiguration();
        configuration.setMoveDirectory("src/test/resources/data/archive");
        MoveFileBroker broker = new MoveFileBroker(this.dryRunModeService);
        broker.setConfiguration(configuration);

        broker.invoke(files);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }

    @Test(expected = EndpointException.class)
    public void test_move_file_dry_run_exception_bad_move_directory() throws IOException {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);

        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBrokerConfiguration configuration = new MoveFileBrokerConfiguration();
        configuration.setMoveDirectory("src/test/resources/data/");
        MoveFileBroker broker = new MoveFileBroker(this.dryRunModeService);
        broker.setConfiguration(configuration);

        broker.invoke(files);
    }

    @Test
    public void test_move_no_target_dir_success() throws IOException {
        when(dryRunModeService.getDryRunMode()).thenReturn(false);

        List<File> files = List.of(new File("src/test/resources/data/test.txt"));

        MoveFileBrokerConfiguration configuration = new MoveFileBrokerConfiguration();
        MoveFileBroker broker = new MoveFileBroker(this.dryRunModeService);
        broker.setConfiguration(configuration);

        broker.invoke(files);

        String[] archiveFiles = new File("src/test/resources/data/archive").list();

        Assert.assertEquals(0, archiveFiles.length);
    }
}
