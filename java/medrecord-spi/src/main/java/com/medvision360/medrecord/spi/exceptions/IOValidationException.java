package com.medvision360.medrecord.spi.exceptions;

import java.io.IOException;

import com.medvision360.medrecord.spi.ValidationReport;

@SuppressWarnings("UnusedDeclaration")
public class IOValidationException extends IOException
{
    private static final long serialVersionUID = 0x130L;
    
    private ValidationException m_delegate;
    
    public IOValidationException(ValidationException delegate)
    {
        super(delegate);
        m_delegate = delegate;
    }

    public ValidationReport getReport()
    {
        return m_delegate == null ? null : m_delegate.getReport();
    }
}
