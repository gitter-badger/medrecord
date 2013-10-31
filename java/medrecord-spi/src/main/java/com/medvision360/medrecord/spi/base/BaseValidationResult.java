package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.ValidationResult;

public class BaseValidationResult implements ValidationResult
{
    private boolean m_valid = true;
    private long m_lineNumber = -1;
    private long m_rowNumber = -1;
    private String m_message = null;
    private String m_fragment = null;
    private String m_path = null;
    private Exception m_details = null;

    public BaseValidationResult(boolean valid, long lineNumber, long rowNumber, String message, String fragment,
            String path, Exception details)
    {
        m_valid = valid;
        m_lineNumber = lineNumber;
        m_rowNumber = rowNumber;
        m_message = message;
        m_fragment = fragment;
        m_path = path;
        m_details = details;
    }

    public BaseValidationResult()
    {
    }

    public BaseValidationResult(String path)
    {
        m_path = path;
    }

    public void setValid(boolean valid)
    {
        m_valid = valid;
    }

    public void setLineNumber(long lineNumber)
    {
        m_lineNumber = lineNumber;
    }

    public void setRowNumber(long rowNumber)
    {
        m_rowNumber = rowNumber;
    }

    public void setMessage(String message)
    {
        m_message = message;
    }

    public void setFragment(String fragment)
    {
        m_fragment = fragment;
    }

    public void setPath(String path)
    {
        m_path = path;
    }

    public void setDetails(Exception details)
    {
        m_details = details;
    }

    @Override
    public boolean isValid()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.isValid()");
    }

    @Override
    public long getLineNumber()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getLineNumber()");
    }

    @Override
    public long getRowNumber()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getRowNumber()");
    }

    @Override
    public String getMessage()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getMessage()");
    }

    @Override
    public String getProblematicFragment()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getProblematicFragment()");
    }

    @Override
    public String getPath()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getPath()");
    }

    @Override
    public Exception getDetails()
    {
        throw new UnsupportedOperationException("todo implement BasicValidationResult.getDetails()");
    }
}
