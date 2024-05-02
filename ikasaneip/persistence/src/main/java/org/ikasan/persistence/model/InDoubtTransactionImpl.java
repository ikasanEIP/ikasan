package org.ikasan.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.ikasan.spec.persistence.model.InDoubtTransaction;

import java.util.Objects;

@Entity
@Table(name="IN_DOUBT")
public class InDoubtTransactionImpl implements InDoubtTransaction {

    @Id
    @Column(name="TRANSACTION_NAME")
    private String transactionName;
    @Column(name="TRANSACTION_STATE")
    private String transactionState;

    @Override
    public String getTransactionName() {
        return transactionName;
    }

    @Override
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    @Override
    public String getTransactionState() {
        return transactionState;
    }

    @Override
    public void setTransactionState(String transactionState) {
        this.transactionState = transactionState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InDoubtTransactionImpl that)) return false;
        return Objects.equals(transactionName, that.transactionName)
            && Objects.equals(transactionState, that.transactionState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionName, transactionState);
    }
}
