package org.ikasan.ootb.data.sharing.module.component.splitter;

import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.ootb.data.sharing.module.component.splitter.configuration.ConfigurationMetaDataSplitterConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.ConfigurationMetaDataTestFixtures;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ConfigurationMetaDataSplitterTest {
    private final ConfigurationMetaDataSplitter splitter = new ConfigurationMetaDataSplitter();

    @Test
    public void batchesInOrderWithoutDroppingOrDuplicatingConfigurations() {
        ModuleAndConfigMetaData source = payload(250);
        var batches = splitter.split(source);
        assertEquals(List.of(100, 100, 50), batches.stream().map(List::size).toList());
        assertEquals(source.getConfigurations(), batches.stream().flatMap(List::stream).toList());
    }

    @Test
    public void supportsConfiguredBatchSizeAndExactMultiples() {
        splitter.getConfiguration().setBatchSize(2);
        assertEquals(List.of(2, 2), splitter.split(payload(4)).stream().map(List::size).toList());
        assertEquals(List.of(1), splitter.split(payload(1)).stream().map(List::size).toList());
    }

    @Test
    public void skipsEmptyAndAbsentConfigurationLists() {
        assertTrue(splitter.split(payload(0)).isEmpty());
        assertTrue(splitter.split(new ModuleAndConfigMetaData(List.of(), null)).isEmpty());
    }

    @Test
    public void batchListsAreIndependentOfTheOriginalList() {
        var source = payload(3);
        splitter.getConfiguration().setBatchSize(2);
        var batches = splitter.split(source);
        source.getConfigurations().clear();
        assertEquals(2, batches.get(0).size());
        batches.get(0).clear();
        assertEquals(1, batches.get(1).size());
    }

    @Test
    public void usesReplacementConfigurationOnTheNextSplit() {
        ModuleAndConfigMetaData source = payload(5);
        assertEquals(List.of(5), splitter.split(source).stream().map(List::size).toList());

        ConfigurationMetaDataSplitterConfiguration configuration = new ConfigurationMetaDataSplitterConfiguration();
        configuration.setBatchSize(2);
        splitter.setConfiguration(configuration);

        var batches = splitter.split(source);
        assertEquals(List.of(2, 2, 1), batches.stream().map(List::size).toList());
        assertEquals(source.getConfigurations(), batches.stream().flatMap(List::stream).toList());
    }

    @Test
    public void batchSizeOneStillEmitsListsForTheRestProducer() {
        ModuleAndConfigMetaData source = payload(3);
        splitter.getConfiguration().setBatchSize(1);

        var batches = splitter.split(source);

        assertEquals(3, batches.size());
        for (int i = 0; i < batches.size(); i++) {
            assertEquals(List.of(source.getConfigurations().get(i)), batches.get(i));
        }
    }

    @Test
    public void acceptsImmutableInputAndDoesNotModifyIt() {
        var source = payload(3);
        List<ConfigurationMetaData> original = List.copyOf(source.getConfigurations());
        source.setConfigurations(original);
        splitter.getConfiguration().setBatchSize(2);

        var batches = splitter.split(source);

        assertEquals(List.of(2, 1), batches.stream().map(List::size).toList());
        assertEquals(original, batches.stream().flatMap(List::stream).toList());
        batches.get(0).clear();
        assertEquals(3, source.getConfigurations().size());
        assertSame(original, source.getConfigurations());
    }

    @Test(expected = SplitterException.class)
    public void rejectsMissingSplitterConfiguration() {
        splitter.setConfiguration(null);
        splitter.split(payload(1));
    }

    @Test(expected = SplitterException.class)
    public void rejectsZeroBatchSize() {
        splitter.getConfiguration().setBatchSize(0);
        splitter.split(payload(1));
    }

    @Test(expected = SplitterException.class)
    public void rejectsNegativeBatchSize() {
        splitter.getConfiguration().setBatchSize(-1);
        splitter.split(payload(1));
    }

    @Test(expected = SplitterException.class)
    public void rejectsNullPayload() {
        splitter.split(null);
    }

    private ModuleAndConfigMetaData payload(int count) {
        List<ConfigurationMetaData> configurations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            configurations.add(ConfigurationMetaDataTestFixtures.createConfigurationMetaData("configuration" + i));
        }
        return new ModuleAndConfigMetaData(List.of(), configurations);
    }
}
