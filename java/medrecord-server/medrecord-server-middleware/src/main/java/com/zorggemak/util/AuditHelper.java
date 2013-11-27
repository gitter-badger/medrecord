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

import com.zorggemak.commons.AuditException;
import com.zorggemak.commons.NoSystemIdAuditException;
import com.zorggemak.commons.NoUserIdAuditException;
import com.zorggemak.commons.ZorgGemakDefines;

import javax.servlet.http.HttpServletRequest;

import static com.zorggemak.util.Configuration.getSetting;

public class AuditHelper {

    public static void auditParam(HttpServletRequest req)
            throws AuditException {
        // todo throw new AuditException("Audit Unsupported");
//
//        String userId = req.getHeader(ZorgGemakDefines.HEADER_USER_ID);
//        if (userId != null && !userId.isEmpty()) {
//            // use it
//        } else if (Boolean.parseBoolean(getSetting("audit.defaults.use", "true"))) {
//            userId = getSetting("audit.defaults.user_id", "00000000-0000-0000-0000-000000000000");
//        } else {
//            throw new NoUserIdAuditException();
//        }
//
//        String systemId = req.getHeader(ZorgGemakDefines.HEADER_SYSTEM_ID);
//        if (systemId != null && !systemId.isEmpty()) {
//            // use it
//        } else if (Boolean.parseBoolean(getSetting("audit.defaults.use", "true"))) {
//            systemId = getSetting("audit.defaults.system_id", "00000000-0000-0000-0000-000000000000");
//        } else {
//            throw new NoSystemIdAuditException();
//        }
//
//        return new AuditTrailParam(userId, systemId);
    }
}
