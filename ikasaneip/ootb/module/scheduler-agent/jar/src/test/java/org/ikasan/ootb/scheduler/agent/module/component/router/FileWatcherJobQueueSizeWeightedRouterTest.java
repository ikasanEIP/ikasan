package org.ikasan.ootb.scheduler.agent.module.component.router;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.component.routing.RouterException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;

public class FileWatcherJobQueueSizeWeightedRouterTest {

    @Mock
    private IBigQueue bigQueue1;
    @Mock
    private IBigQueue bigQueue2;
    @Mock
    private IBigQueue bigQueue3;
    @Mock
    private IBigQueue bigQueue4;

    private FileWatcherJobQueueSizeWeightedRouter router;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        router = new FileWatcherJobQueueSizeWeightedRouter();
    }

    @Test
    public void test_route_success() throws RouterException {

        when(bigQueue1.size()).thenReturn(10L);
        when(bigQueue2.size()).thenReturn(11L);
        when(bigQueue3.size()).thenReturn(1L);
        when(bigQueue4.size()).thenReturn(0L);

        // Set the instance of InternalFileWatcherJobQueueCache to the mocked object
        InternalFileWatcherJobQueueCache.instance().put("queue-name-1", bigQueue1);
        InternalFileWatcherJobQueueCache.instance().put("queue-name-2", bigQueue2);
        InternalFileWatcherJobQueueCache.instance().put("queue-name-3", bigQueue3);
        InternalFileWatcherJobQueueCache.instance().put("queue-name-4", bigQueue4);

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        String result = router.route(fileWatcherJobEvent);

        Assert.assertEquals("queue-name-4", result);
    }

    @Test
    public void test_exception_no_routes() throws RouterException {
        // Set the instance of InternalFileWatcherJobQueueCache to the mocked object
        InternalFileWatcherJobQueueCache.instance().removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        String result = router.route(fileWatcherJobEvent);

        Assert.assertEquals("INVALID_ROUTE", result);
    }
}
