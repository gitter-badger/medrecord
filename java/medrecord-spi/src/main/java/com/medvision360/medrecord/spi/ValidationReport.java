package com.medvision360.medrecord.spi;

public interface ValidationReport {
    public boolean isValid();
    
    public Iterable<ValidationResult> getReport();
    
    public Iterable<ValidationResult> getErrors();
}
