package com.medrecord.spi;

import com.medrecord.spi.exceptions.TransactionException;

public interface TransactionalService {
    public boolean supportsTransactions();
    public void begin() throws TransactionException;
    public void commit() throws TransactionException;
    public void rollback() throws TransactionException;    
}
