package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.spi.ValidationReport;
import com.medvision360.medrecord.spi.ValidationResult;
import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class ValidationExceptionTest extends ExceptionTestBase<ValidationException> {
    
    ValidationReport report = new ValidationReport() {
        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Iterable<ValidationResult> getReport() {
            return null;
        }

        @Override
        public Iterable<ValidationResult> getErrors() {
            return null;
        }
    };
    
    @Override
    protected Class<ValidationException> getExceptionClass() {
        return ValidationException.class;
    }
    
    public void testAdditionalConstructors() {
        ValidationException e;
        e = new ValidationException(report);
        assertEquals(report, e.getReport());
        
        e = new ValidationException(msg, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));
        
        e = new ValidationException(msg, cause, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));
        assertEquals(cause, e.getCause());
        
        e = new ValidationException(msg, cause, true, true, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));
        assertEquals(cause, e.getCause());
    }
}
