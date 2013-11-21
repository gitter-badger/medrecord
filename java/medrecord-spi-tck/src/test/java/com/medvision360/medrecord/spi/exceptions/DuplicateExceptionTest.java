package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class DuplicateExceptionTest extends ExceptionTestBase<DuplicateException>
{
    @Override
    protected Class<DuplicateException> getExceptionClass()
    {
        return DuplicateException.class;
    }
}
