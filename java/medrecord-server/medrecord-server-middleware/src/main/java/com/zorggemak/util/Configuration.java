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
package com.zorggemak.util;

import com.zorggemak.commons.ZorgGemakDefines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static final String PROP_PATH = "/etc/medvision360/middleware";
    private static final String PROP_FILE = "/configuration.properties";

    private static Properties settings = null;

    public static void init() {
        if (settings != null) {
            return;
        }

        FileInputStream fis = null;
        try {
            settings = new Properties();
            File pFile = new File(PROP_PATH + PROP_FILE);
            if (pFile.exists()) {
                log.debug("configuration from file " + pFile.getAbsolutePath());
                fis = new FileInputStream(pFile);
                settings.load(fis);
            } else {
                log.debug("configuration from system resource /middleware" + PROP_FILE);
                settings.load(Configuration.class.getResourceAsStream("/middleware" + PROP_FILE));
            }
        } catch (IOException e) {
            log.error("Problem reading configuration", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        initStaticVariables();
    }

    public static void initStaticVariables() {
        ZorgGemakDefines.USE_VALIDATION = Boolean.parseBoolean(getSetting("validation.enabled", "true"));
        ZorgGemakDefines.APPLICATION_VERSION = getSetting("version", ZorgGemakDefines.APPLICATION_VERSION);
    }

    public static String getSetting(String name) {
        if (settings == null) {
            log.error(String.format("Attempt to access setting %s before configuration has been read", name));
            return null;
        }
        return settings.getProperty(name);
    }

    public static String getSetting(String name, String defaultValue) {
        if (settings == null) {
            log.warn(String.format("Attempt to access setting %s before configuration has been read", name));
            return defaultValue;
        }
        return settings.getProperty(name, defaultValue);
    }

}
