package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class NotSupportedExceptionTest extends ExceptionTestBase<NotSupportedException> {
    @Override
    protected Class<NotSupportedException> getExceptionClass() {
        return NotSupportedException.class;
    }
}
