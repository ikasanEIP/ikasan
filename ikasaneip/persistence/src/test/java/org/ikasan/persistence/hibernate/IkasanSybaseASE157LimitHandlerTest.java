package org.ikasan.persistence.hibernate;

import org.hibernate.engine.spi.RowSelection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Ikasan Development Team on 29/12/2017.
 */
class IkasanSybaseASE157LimitHandlerTest
{
    @Test
    void test_no_first_row_no_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler();

        String resultantSql = handler.processSql(sql, rowSelection);

        assertEquals(sql, resultantSql, "Sql must equal");
    }

    @Test
    void test_no_first_row_with_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();
        rowSelection.setMaxRows(1000);

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler();

        String resultantSql = handler.processSql(sql, rowSelection);

        assertEquals("select top 1000 * from SomeTable", resultantSql, "Sql must equal");
    }

    @Test
    void test_with_first_row_with_max_rows()
    {
        String sql = "select * from SomeTable";

        RowSelection rowSelection = new RowSelection();
        rowSelection.setMaxRows(1000);
        rowSelection.setFirstRow(500);

        IkasanSybaseASE157LimitHandler handler = new IkasanSybaseASE157LimitHandler();

        String resultantSql = handler.processSql(sql, rowSelection);

        assertEquals("select top 1500 * from SomeTable", resultantSql, "Sql must equal");
    }
}
