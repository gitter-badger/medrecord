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

import java.util.Arrays;

@SuppressWarnings("FieldCanBeLocal")
public class AutoGenerationIntegrationTest extends AbstractAutoGenerationIntegrationTest
{
    static {
        archetypesToSkip.addAll(Arrays.asList(
                // recursion requiring an instance of themselves as content...
                "openEHR-EHR-ACTION.follow_up.v1",
                "openEHR-EHR-ACTION.imaging.v1",
                "openEHR-EHR-ACTION.intravenous_fluid_administration.v1",
                
                // requires openEHR-EHR-SECTION.medications.v1 which we don't have
                "openEHR-EHR-COMPOSITION.prescription.v1"
        ));
    }

    @Override
    protected void loadAll() throws Exception
    {
        m_archetypeStore.clear();
        m_archetypeLoader.loadAll("unittest"); // always needed, contains EHRSTATUS we use
        m_archetypeLoader.loadAll("openehr");
    }

}
