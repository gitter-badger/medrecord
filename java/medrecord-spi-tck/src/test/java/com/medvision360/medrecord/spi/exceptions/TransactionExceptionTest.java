package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class TransactionExceptionTest extends ExceptionTestBase<TransactionException> {
    @Override
    protected Class<TransactionException> getExceptionClass() {
        return TransactionException.class;
    }
}
