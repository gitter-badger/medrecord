package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

public interface EHRStore extends TransactionalService, StatusService // todo javadoc / api spec / exceptions
{
    public HierObjectID getSystemID();
    
    public EHR get(HierObjectID id) throws NotFoundException, IOException, ParseException;

    public EHR insert(EHR EHR)
            throws DuplicateException, NotSupportedException, IOException, SerializeException;

    public void delete(HierObjectID id) throws NotFoundException, IOException, ParseException, SerializeException;

    public void undelete(HierObjectID id) throws NotFoundException, IOException, ParseException, SerializeException;

    public boolean has(HierObjectID id) throws IOException, ParseException;

    public Iterable<HierObjectID> list() throws IOException;

    public void initialize() throws IOException;

    public void clear() throws IOException;
}
