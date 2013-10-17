/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import com.medvision360.medrecord.spi.exceptions.TransactionException;

public interface TransactionalService
{
    public boolean supportsTransactions();

    public void begin() throws TransactionException;

    public void commit() throws TransactionException;

    public void rollback() throws TransactionException;
}
