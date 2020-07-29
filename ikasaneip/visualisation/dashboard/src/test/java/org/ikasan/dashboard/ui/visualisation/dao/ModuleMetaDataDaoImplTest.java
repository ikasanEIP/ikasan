package org.ikasan.dashboard.ui.visualisation.dao;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ModuleMetaDataDaoImplTest
{
    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);
    }

    @Test
    public void test()
    {
        ModuleMetaDataDaoImpl dao = new ModuleMetaDataDaoImpl();

        System.out.println(dao.getModuleMetaData("blbgToms-mhiTrade"));
    }
}
