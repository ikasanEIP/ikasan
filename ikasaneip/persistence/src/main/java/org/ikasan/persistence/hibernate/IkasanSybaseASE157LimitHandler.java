package org.ikasan.persistence.hibernate;

import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.engine.spi.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Ikasan Development Team on 27/12/2017.
 */
public class IkasanSybaseASE157LimitHandler extends AbstractLimitHandler
{
    public IkasanSybaseASE157LimitHandler(String sql, RowSelection selection)
    {
        super(sql, selection);
    }

    @Override
    public String getProcessedSql()
    {
        String select = sql.substring(0, sql.indexOf("select") + "select".length());
        String statement = sql.substring(sql.indexOf("select") + "select".length(), sql.length());

        if(selection.getFirstRow() != null && selection.getMaxRows() != null)
        {
            return select + " top " + (selection.getFirstRow() + selection.getMaxRows()) + statement;
        }
        else if(selection.getMaxRows() != null)
        {
            return select + " top " +  selection.getMaxRows() + statement;
        }
        else
        {
            return sql;
        }
    }

    @Override
    public boolean supportsLimit()
    {
        return true;
    }

    public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) throws SQLException
    {
        return 0;
    }

    public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) throws SQLException {
        return 0;
    }

}
