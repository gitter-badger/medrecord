package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class StatusExceptionTest extends ExceptionTestBase<StatusException>
{
    @Override
    protected Class<StatusException> getExceptionClass()
    {
        return StatusException.class;
    }
}
