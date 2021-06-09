package org.ikasan.connector.basefiletransfer.net;

import junit.framework.TestCase;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientListEntryTest extends TestCase {


    @Test
    public void testEvalRelativePath(){
        ClientListEntry clientListEntry = new ClientListEntry();
        clientListEntry.setFullPath("/test-data/sftp/trade/2021-03-15/202103152100502/trade_2021-03-15_20210315210502_1.csv");
        assertEquals("trade/2021-03-15/202103152100502", clientListEntry.evalRelativePath("//test-data/sftp"));
    }
}