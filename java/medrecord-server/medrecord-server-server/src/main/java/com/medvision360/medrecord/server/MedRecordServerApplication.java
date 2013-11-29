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
package com.medvision360.medrecord.server;

import com.medvision360.lib.server.RestletApplication;
import com.medvision360.lib.server.config.ConfigurationException;
import com.medvision360.lib.server.config.ConfigurationWrapper;
import com.medvision360.medrecord.server.archetype.ArchetypeListServerResource;
import com.medvision360.medrecord.server.archetype.ArchetypeServerResource;
import com.medvision360.medrecord.server.ehr.EHRListServerResource;
import com.medvision360.medrecord.server.ehr.EHRLocatableListServerResource;
import com.medvision360.medrecord.server.ehr.EHRServerResource;
import com.medvision360.medrecord.server.ehr.EHRUndeleteServerResource;
import com.medvision360.medrecord.server.locatable.LocatableEHRServerResource;
import com.medvision360.medrecord.server.locatable.LocatableListServerResource;
import com.medvision360.medrecord.server.locatable.LocatableServerResource;
import com.medvision360.medrecord.server.query.QueryEHRServerResource;
import com.medvision360.medrecord.server.query.QueryLocatableServerResource;
import com.medvision360.medrecord.server.query.XQueryLocatableServerResource;
import com.medvision360.medrecord.server.query.XQueryServerResource;
import com.medvision360.medrecord.server.test.TestClearServerResource;
import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.service.TunnelService;

@SuppressWarnings("UnusedDeclaration")
public class MedRecordServerApplication extends RestletApplication
{
    public MedRecordServerApplication()
    {
        // add a custom status service which understands the exception conventions used in medrecord
        setStatusService(new CustomStatusService());
        
        // need to enable preferences and extensions tunnel to be able
        // to use document.en.json
        //
        setTunnelService(
            new TunnelService(
                true,   // enabled
                false,  // method tunnel
                true,   // preferences tunnel
                false,  // query tunnel
                // unfortunately, it seems the extension mapping triggers
                //   https://github.com/restlet/restlet-framework-java/issues/801
                //true,   // extensions tunnel
                false,
                false,  // user agent tunnel
                false   // headers tunnel
            )
        );
    }

    /**
     * Initialize the application.
     */
    @Override
    public void init(final ConfigurationWrapper config)
        throws ConfigurationException
    {
        enableJackson();

        MedRecordService service = new MedRecordService();
        getServices().add(service);
    }

    /**
     * Create all resources.
     */
    @Override
    public Restlet makeInboundRoot()
    {
        final Router root = new Router(getContext());

        //noinspection SpellCheckingInspection
        root.attach(  "/apidocs",                               new Directory(getContext(), "war:///apidocs")        );
        root.attach(  "/archetype",                             tx(ArchetypeListServerResource.class)                );
        root.attach(  "/archetype/{id}",                        tx(ArchetypeServerResource.class)                    );
        // todo        /archetype/{id}/terms
        // todo        /terms
        // todo        /measurements
        root.attach(  "/ehr",                                   tx(EHRListServerResource.class)                      );
        root.attach(  "/ehr/{id}",                              tx(EHRServerResource.class)                          );
        root.attach(  "/ehr/{id}/undelete",                     tx(EHRUndeleteServerResource.class)                  );
        root.attach(  "/ehr/{id}/locatable",                    tx(EHRLocatableListServerResource.class)             );
        // todo        /ehr/{id}/summary
        // todo        /ehr/{id}/audit
        root.attach(  "/locatable",                             tx(LocatableListServerResource.class)                );
        root.attach(  "/locatable/{id}",                        tx(LocatableServerResource.class)                    );
        root.attach(  "/locatable/{id}/ehr",                    tx(LocatableEHRServerResource.class)                 );
        // todo        /locatable/{id}/summary
        // todo        /locatable/{id}/audit
        root.attach(  "/query/locatable",                       tx(QueryLocatableServerResource.class)               );
        root.attach(  "/query/ehr",                             tx(QueryEHRServerResource.class)                     );
        root.attach(  "/query/xquery",                          tx(XQueryServerResource.class)                       );
        root.attach(  "/query/xquery/locatable",                tx(XQueryLocatableServerResource.class)              );
        root.attach(  "/test/clear",                            tx(TestClearServerResource.class)                    );
        // todo        /audit

        return root;
    }

    private TransactionFilter tx(Class<? extends ServerResource> targetClass)
    {
        return new TransactionFilter(getContext(), targetClass);
    }
}
