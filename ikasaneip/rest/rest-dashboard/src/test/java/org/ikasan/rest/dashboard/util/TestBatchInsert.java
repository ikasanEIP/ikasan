package org.ikasan.rest.dashboard.util;

import org.ikasan.rest.dashboard.ErrorController;
import org.ikasan.spec.persistence.BatchInsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestBatchInsert implements BatchInsert
{
    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private int size = 0;

    @Override
    public void insert(List entities)
    {
        entities.forEach(o -> logger.info("Inserting entity: " + o));

        size = entities.size();
    }

    public int getSize()
    {
        return size;
    }
}
