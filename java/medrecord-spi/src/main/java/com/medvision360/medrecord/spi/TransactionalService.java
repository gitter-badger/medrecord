package com.medvision360.medrecord.spi;

import com.medvision360.medrecord.spi.exceptions.TransactionException;

public interface TransactionalService {
    public boolean supportsTransactions();
    public void begin() throws TransactionException;
    public void commit() throws TransactionException;
    public void rollback() throws TransactionException;    
}
