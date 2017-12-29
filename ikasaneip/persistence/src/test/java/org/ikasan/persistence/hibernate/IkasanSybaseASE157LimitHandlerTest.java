package org.ikasan.persistence.hibernate;

import org.hibernate.engine.spi.RowSelection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Ikasan Development Team on 29/12/2017.
 */
public class IkasanSybaseASE157LimitHandlerTest
{
    @Test
    public void test_no_first_row_no_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler(sql, rowSelection);

        String resultantSql = handler.getProcessedSql();

        Assert.assertEquals("Sql must equal", sql, resultantSql);
    }

    @Test
    public void test_no_first_row_with_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();
        rowSelection.setMaxRows(1000);

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler(sql, rowSelection);

        String resultantSql = handler.getProcessedSql();

        Assert.assertEquals("Sql must equal", "select top 1000 * from SomeTable", resultantSql);
    }

    @Test
    public void test_with_first_row_with_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();
        rowSelection.setMaxRows(1000);
        rowSelection.setFirstRow(500);

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler(sql, rowSelection);

        String resultantSql = handler.getProcessedSql();

        Assert.assertEquals("Sql must equal", "select top 1500 * from SomeTable", resultantSql);
    }
}
