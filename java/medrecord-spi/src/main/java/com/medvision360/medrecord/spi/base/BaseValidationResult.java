/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.api.ValidationResult;

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
        return m_valid;
    }

    @Override
    public long getLineNumber()
    {
        return m_lineNumber;
    }

    @Override
    public long getRowNumber()
    {
        return m_rowNumber;
    }

    @Override
    public String getMessage()
    {
        return m_message;
    }

    @Override
    public String getProblematicFragment()
    {
        return m_fragment;
    }

    @Override
    public String getPath()
    {
        return m_path;
    }

    @Override
    public Exception getDetails()
    {
        return m_details;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("{ValidationResult:");
        str.append(isValid() ? "valid" : "invalid");
        str.append(",path=").append(m_path);
        str.append(",message=").append(m_message);
        str.append("}");
        return str.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof BaseValidationResult))
        {
            return false;
        }

        BaseValidationResult that = (BaseValidationResult) o;

        if (m_lineNumber != that.m_lineNumber)
        {
            return false;
        }
        if (m_rowNumber != that.m_rowNumber)
        {
            return false;
        }
        if (m_valid != that.m_valid)
        {
            return false;
        }
        // we don't want to match up stack traces and such
        if (!shallowExceptionEquals(that))
        {
            return false;
        }
        if (m_fragment != null ? !m_fragment.equals(that.m_fragment) : that.m_fragment != null)
        {
            return false;
        }
        if (m_message != null ? !m_message.equals(that.m_message) : that.m_message != null)
        {
            return false;
        }
        if (m_path != null ? !m_path.equals(that.m_path) : that.m_path != null)
        {
            return false;
        }

        return true;
    }

    private boolean shallowExceptionEquals(BaseValidationResult that)
    {
        if (m_details == null)
        {
            return that.m_details == null;
        }
        if (that == null)
        {
            return false;
        }
        if (!m_details.getClass().isAssignableFrom(that.m_details.getClass()))
        {
            return false;
        }
        String message = m_details.getMessage();
        String thatMessage = that.m_details.getMessage();
        return message == null ? thatMessage == null : message.equals(thatMessage);
    }

    @Override
    public int hashCode()
    {
        int result = (m_valid ? 1 : 0);
        result = 31 * result + (int) (m_lineNumber ^ (m_lineNumber >>> 32));
        result = 31 * result + (int) (m_rowNumber ^ (m_rowNumber >>> 32));
        result = 31 * result + (m_message != null ? m_message.hashCode() : 0);
        result = 31 * result + (m_fragment != null ? m_fragment.hashCode() : 0);
        result = 31 * result + (m_path != null ? m_path.hashCode() : 0);
        //result = 31 * result + (m_details != null ? m_details.hashCode() : 0);
        return result;
    }
}
