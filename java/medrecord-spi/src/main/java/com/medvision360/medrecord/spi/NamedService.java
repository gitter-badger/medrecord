package com.medvision360.medrecord.spi;

/**
 * Simple interface to be implemented by services that have a name. No guarantees are made or expected as to service 
 * name uniqueness within a service.
 */
public interface NamedService {
    /**
     * Get the name of this service. Should be both machine- and human-readable. 
     * 
     * @return the name of this service.
     */
    public String getName();
}
