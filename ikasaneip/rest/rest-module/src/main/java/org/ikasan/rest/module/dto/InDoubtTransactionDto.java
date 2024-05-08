package org.ikasan.rest.module.dto;

import java.io.Serializable;

public class InDoubtTransactionDto implements Serializable {
    private String transactionName;
    private String transactionState;

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(String transactionState) {
        this.transactionState = transactionState;
    }
}
