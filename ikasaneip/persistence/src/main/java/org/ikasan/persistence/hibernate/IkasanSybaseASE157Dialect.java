package org.ikasan.persistence.hibernate;

import org.hibernate.dialect.SybaseASE157Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;

/**
 * Created by stewmi on 27/12/2017.
 */
public class IkasanSybaseASE157Dialect extends SybaseASE157Dialect
{
    @Override
    public boolean supportsLimit()
    {
        return true;
    }

    @Override
    public LimitHandler buildLimitHandler(String sql, RowSelection selection)
    {
        return new IkasanSybaseASE157LimitHandler(sql, selection);
    }
}
