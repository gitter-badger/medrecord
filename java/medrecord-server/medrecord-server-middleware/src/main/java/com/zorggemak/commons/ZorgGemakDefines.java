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
package com.zorggemak.commons;

public class ZorgGemakDefines {
    public static String APPLICATION_VERSION = "0.0.1";
    public static final String HEADER_USER_ID = "X-On-Behalf-Of";
    public static final String HEADER_SYSTEM_ID = "X-Originating-System-Id";
    public static final long REFER_TIMEOUT = 30 * 60 * 1000;
    public static boolean USE_VALIDATION = true;
}
