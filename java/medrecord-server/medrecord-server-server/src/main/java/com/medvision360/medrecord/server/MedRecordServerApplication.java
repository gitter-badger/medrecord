/**
 * This file is part of MEDvision360 Profile Server..
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.server;

import com.medvision360.medrecord.server.resources.ArchetypeListServerResource;
import com.medvision360.medrecord.server.resources.ArchetypeServerResource;
import com.medvision360.medrecord.server.resources.TestClearServerResource;
import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.service.TunnelService;

import com.medvision360.lib.server.RestletApplication;
import com.medvision360.lib.server.config.ConfigurationException;
import com.medvision360.lib.server.config.ConfigurationWrapper;

@SuppressWarnings("UnusedDeclaration")
public class MedRecordServerApplication extends RestletApplication
{
    public MedRecordServerApplication()
    {
        // add a custom status service which understands the exception conventions used in medrecord
        setStatusService(new CustomStatusService());
        
        // need to enable preferences and extensions tunnel to be able
        // to use document.en.json
        setTunnelService(
            new TunnelService(
                true,   // enabled
                false,  // method tunnel
                true,   // preferences tunnel
                false,  // query tunnel
                true,   // extensions tunnel
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
        root.attach("/apidocs", new Directory(getContext(), "war:///apidocs"));

        root.attach(
                "/archetype",
                new TransactionFilter(
                        getContext(),
                        ArchetypeListServerResource.class
                )
        );

        root.attach(
                "/archetype/{id}",
                new TransactionFilter(
                        getContext(),
                        ArchetypeServerResource.class
                )
        );

        root.attach(
                "/test/clear",
                new TransactionFilter(
                        getContext(),
                        TestClearServerResource.class
                )
        );

        return root;
    }
}
