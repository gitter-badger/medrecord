package com.zorggemak.util;

import com.medvision360.kernel.engine.audit.AuditTrailParam;
import com.zorggemak.commons.AuditException;
import com.zorggemak.commons.NoSystemIdAuditException;
import com.zorggemak.commons.NoUserIdAuditException;
import com.zorggemak.commons.ZorgGemakDefines;

import javax.servlet.http.HttpServletRequest;

import static com.zorggemak.util.Configuration.getSetting;

public class AuditHelper {

    public static AuditTrailParam auditParam(HttpServletRequest req)
            throws AuditException {

        String userId = req.getHeader(ZorgGemakDefines.HEADER_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            // use it
        } else if (Boolean.parseBoolean(getSetting("audit.defaults.use", "true"))) {
            userId = getSetting("audit.defaults.user_id", "00000000-0000-0000-0000-000000000000");
        } else {
            throw new NoUserIdAuditException();
        }

        String systemId = req.getHeader(ZorgGemakDefines.HEADER_SYSTEM_ID);
        if (systemId != null && !systemId.isEmpty()) {
            // use it
        } else if (Boolean.parseBoolean(getSetting("audit.defaults.use", "true"))) {
            systemId = getSetting("audit.defaults.system_id", "00000000-0000-0000-0000-000000000000");
        } else {
            throw new NoSystemIdAuditException();
        }

        return new AuditTrailParam(userId, systemId);
    }
}
