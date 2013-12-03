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
package com.medvision360.medrecord.itest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.api.ValidationResult;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.spi.WrappedArchetype;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;

public abstract class AbstractAutoGenerationIntegrationTest extends AbstractIntegrationTest
{
    public final static Set<String> archetypesToSkip;
    static {
        archetypesToSkip = new HashSet<>();
    }
    
    int skipped = 0;
    int generated = 0;
    int creationFailed = 0;
    int creationUnsupported = 0;
    int inserted = 0;
    int totalIDs = 0;
    int retrieved = 0;
    int serializeFailed = 0;
    int internalUIDs = 0;
    int serialized = 0;
    int validated = 0;
    int notValidated = 0;
    int valid = 0;
    int invalid = 0;
    int validRule = 0;
    int invalidRule = 0;
    long generatedNs = 0;
    long insertedNs = 0;
    long retrievedNs = 0;
    long serializedNs = 0;
    long validatedNs = 0;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        skipped = 0;
        generated = 0;
        creationFailed = 0;
        creationUnsupported = 0;
        inserted = 0;
    
        totalIDs = 0;
        retrieved = 0;
        serializeFailed = 0;
        internalUIDs = 0;
        serialized = 0;
    
        validated = 0;
        notValidated = 0;
        valid = 0;
        invalid = 0;
        validRule = 0;
        invalidRule = 0;

        generatedNs = 0;
        insertedNs = 0;
        retrievedNs = 0;
        serializedNs = 0;
        validatedNs = 0;
    }

    public void testEverything() throws Exception
    {
        loadAll();

        generateAll();

        serializeAll();

        validateAll();
        
        useEngineApi();

        report();
        
        assertEquals("No failures", 0, creationFailed);
    }

    /**
     * Override this to populate {@link #m_archetypeStore} with archetypes, perhaps using {@link #m_archetypeLoader} 
     * to do so. 
     */
    protected abstract void loadAll() throws Exception;

    protected void generateAll() throws Exception
    {
        subject = subject();
        Archetyped arch = new Archetyped(new ArchetypeID(EHRSTATUS_ARCHETYPE), "1.0.2");
        EHRStatus status = new EHRStatus(makeUID(), "at0001", text("EHR Status"),
                arch, null, null, null, subject, true, true, null);

        EHR createdEHR = m_engine.createEHR(status);
        EHRStatus retrievedStatus = m_engine.getEHRStatus(createdEHR);
        EHRStatus retrievedByIdStatus = m_engine.getEHRStatus(new HierObjectID(retrievedStatus.getUid().getValue()));
        assertEqualish(retrievedStatus, retrievedByIdStatus);

        Iterable<ArchetypeID> allArchetypeIDs = m_archetypeStore.list();
        TreeSet<String> sortedIDs = new TreeSet<>();
        Iterables.addAll(sortedIDs, Iterables.transform(allArchetypeIDs, new Function<ArchetypeID, String>()
        {
            public String apply(ArchetypeID input)
            {
                return input.getValue();
            }
        }));
        OUTER:
        for (String archetypeName : sortedIDs)
        {
            ArchetypeID archetypeID = new ArchetypeID(archetypeName);
            
            if (archetypesToSkip.contains(archetypeID.getValue()))
            {
                log.info(String.format("Skipping problematic archetype %s", archetypeID.getValue()));
                skipped++;
                continue;
            }
            
            WrappedArchetype wrappedArchetype = m_archetypeStore.get(archetypeID);
            Archetype archetype = wrappedArchetype.getArchetype();
            Locatable generated = generate(archetype);
            insert(createdEHR, generated);
        }
    }

    protected Locatable generate(Archetype archetype) throws Exception
    {
        long ct;
        
        String archetypeName = archetype.getArchetypeId().getValue();
        
        // generate
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Generating instance of %s", archetypeName));
        }
        Locatable instance;
        try
        {
            ct = System.nanoTime();
            instance = m_locatableGenerator.generate(archetype);
            generatedNs += System.nanoTime() - ct;

            generated++;
        }
        catch (Exception e)
        {
            String message = e.getMessage();
            log.error(String.format("FAILED generating instance of %s: %s", archetypeName, message));
            creationFailed++;
            return null;
        }
        String className = instance.getClass().getSimpleName();
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Got %s for archetype %s", className, archetypeName));
        }
        
        return instance;
    }

    protected Locatable insert(EHR EHR, Locatable instance) throws Exception
    {
        long ct = System.nanoTime();
        Locatable insertedInstance = m_locatableStore.insert(EHR, instance);
        insertedNs += System.nanoTime() - ct;

        inserted++;
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Inserted a %s", instance.getArchetypeDetails().getArchetypeId().getValue()));
        }
        
        return insertedInstance;
    }

    protected void serializeAll() throws IOException
    {
        Iterable<HierObjectID> allIDs = m_locatableStore.list();
        File base = new File("build" + File.separatorChar + "ser");
        if (!base.exists())
        {
            base.mkdirs();
        }
        for (HierObjectID hierObjectID : allIDs)
        {
            serialize(base, hierObjectID);
        }
    }

    protected void serialize(File base, HierObjectID hierObjectID)
    {
        totalIDs++;
        String id = null;
        try
        {
            id = hierObjectID.root().getValue();

            long ct = System.nanoTime();
            Locatable locatable = m_locatableStore.get(hierObjectID);
            retrievedNs += System.nanoTime() - ct;

            retrieved++;
            
            File target = new File(base, id + ".json");
            FileOutputStream fis = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fis, 1024*16);
            
            ct = System.nanoTime();
            m_pvSerializer.serialize(locatable, bos);
            serializedNs += System.nanoTime() - ct;
            
            bos.flush();
            fis.close();

            serialized++;
        }
        catch (NotFoundException e)
        {
            internalUIDs++;
        }
        catch (IOException | ParseException | SerializeException e)
        {
            serializeFailed++;
            log.error(String.format("Difficulty serializing %s: %s", id, e.getMessage())); //, e);
        }
    }

    protected void validateAll() throws IOException, ParseException, NotSupportedException
    {
        long ct = System.nanoTime();
        for (HierObjectID hierObjectID : m_locatableStore.list())
        {
            Locatable locatable;
            try
            {
                locatable = m_locatableStore.get(hierObjectID);
            }
            catch (NotFoundException e)
            {
                continue;
            }
            try
            {
                validate(locatable);
            }
            catch (NotSupportedException e)
            {
                log.error(e.getMessage(), e);
                notValidated++;
            }
        }
        validatedNs += System.nanoTime() - ct;
    }

    protected void validate(Locatable locatable) throws NotSupportedException
    {
        ValidationReport report = m_locatableValidator.validate(locatable);
        validated++;

        if (!report.isValid())
        {
            System.out.println("=================");
            System.out.println("VALIDATION REPORT");
            System.out.println("=================");
            System.out.println("violations: " + Iterables.size(report.getErrors()));
            System.out.println();
        }
        Iterable<ValidationResult> results = report.getReport();
        for (ValidationResult result : results)
        {
            if (!report.isValid())
            {
                if (!result.isValid())
                {
                    System.out.println(result.getMessage());
                }
            }
            if (result.isValid())
            {
                validRule++;
            }
            else
            {
                invalidRule++;
            }
        }
        if (!report.isValid())
        {
            System.out.println();
            invalid++;
        }
        else
        {
            valid++;
        }
    }

    protected void useEngineApi() throws Exception
    {
        Iterable<HierObjectID> ehrIDs = m_engine.getEHRStore().list();
        for (HierObjectID ehrID : ehrIDs)
        {
            EHR EHR = m_engine.getEHRStore().get(ehrID);
            Iterable<HierObjectID> locatableIDs;
            try
            {
                locatableIDs = m_engine.getLocatableStore().list(EHR);
            }
            catch(NotFoundException e)
            {
                // ignored
                continue;
            }
            Set<String> rmTypes = new HashSet<>();
            for (HierObjectID locatableID : locatableIDs)
            {
                Locatable locatable = m_engine.getLocatableStore().get(locatableID);
                assertNotNull(locatable);
                rmTypes.add(locatable.getArchetypeDetails().getArchetypeId().rmEntity());
            }
            for (String rmType : rmTypes)
            {
                locatableIDs = m_engine.getLocatableStore().list(EHR, rmType);
                for (HierObjectID locatableID : locatableIDs)
                {
                    Locatable locatable = m_engine.getLocatableStore().get(locatableID);
                    assertNotNull(locatable);
                    String retrievedRmType = locatable.getArchetypeDetails().getArchetypeId().rmEntity();
                    assertEquals(rmType, retrievedRmType);
                }
            }
        }
    }

    protected void report() throws IOException
    {
        int storedInMemory = Iterables.size(m_fallbackStore.list());

        log.info(String.format("Created %s instances using skeleton generation (skipped %s, failed %s, " +
                "unsupported %s)",
                generated, skipped, creationFailed, creationUnsupported));
        log.info(String.format("Inserted %s locatables (%s in xml databases)",
                inserted, inserted - storedInMemory));
        log.info(String.format("Serialized %s instances (total %s, retrieved %s, failed %s, internal %s)",
                serialized, totalIDs, retrieved, serializeFailed, internalUIDs));
        log.info(String.format("Validated %s instances (valid %s, invalid %s, valid rules %s, invalid rules %s, " +
                "not validated %s)",
                validated, valid, invalid, validRule, invalidRule, notValidated));
        log.info(String.format("generated/s %.2f, inserted/s %.2f, validated/s %.2f, " +
                "retrieved/s %.2f, serialized/s %.2f",
                generated/(generatedNs/10E8),
                inserted/(insertedNs/10E8),
                validated/(validatedNs/10E8),
                retrieved/(retrievedNs/10E8),
                serialized/(serializedNs/10E8)
                ));
    }
}
