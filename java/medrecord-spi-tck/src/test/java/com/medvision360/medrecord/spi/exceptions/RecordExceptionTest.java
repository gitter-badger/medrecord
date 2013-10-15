package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class RecordExceptionTest extends ExceptionTestBase<RecordException> {
    @Override
    protected Class<RecordException> getExceptionClass() {
        return RecordException.class;
    }
}
