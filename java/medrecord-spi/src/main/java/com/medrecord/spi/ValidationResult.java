package com.medrecord.spi;

public interface ValidationResult {
    public boolean isValid();
    
    public long getLineNumber();
    
    public long getRowNumber();
    
    public String getMessage();
    
    public String getProblematicFragment();
    
    public String getPath();
    
    public Exception getDetails();
}
