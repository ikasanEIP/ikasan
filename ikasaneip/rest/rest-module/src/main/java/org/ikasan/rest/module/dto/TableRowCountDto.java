package org.ikasan.rest.module.dto;

import java.io.Serializable;

public class TableRowCountDto implements Serializable {
    private String tableName;
    private int rowCount;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
}
