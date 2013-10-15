/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright (c) 2004 Acode HB, Sweden, Copyright (c) 2013 MEDvision360, The Netherlands.
 *   Licensed under the MPL 1.1/GPL 2.0/LGPL 2.1.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.RMObject;
import org.openehr.rm.common.generic.PartyProxy;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * This is a variant of {@link AuditInfo} that has no auditType or description. It's used to in the {@link 
 * AuditedService} API to set audit information on a set of operations while avoiding the repetition of the same 
 * details again and again.
 *
 * @author Rong Chen
 * @author Leo Simons
 */
public class AuditInfo extends RMObject {
    private static final long serialVersionUID = 0x130L;
    
    @FullConstructor
    public AuditInfo(@Attribute(name = "systemId", required = true) String systemId,
                      @Attribute(name = "committer", required = true) PartyProxy committer,
                      @Attribute(name = "auditTime", required = true) DvDateTime auditTime) {
        if (StringUtils.isEmpty(systemId)) {
            throw new IllegalArgumentException("empty systemId");
        }
        if (committer == null) {
            throw new IllegalArgumentException("null committer");
        }
        if (auditTime == null) {
            throw new IllegalArgumentException("null auditTime");
        }
        this.systemId = systemId;
        this.committer = committer;
        this.auditTime = auditTime;
    }

    public String getSystemId() {
        return systemId;
    }

    public PartyProxy getCommitter() {
        return committer;
    }

    public DvDateTime getAuditTime() {
        return auditTime;
    }

    // POJO start
    protected AuditInfo() {
    }

    void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    void setCommitter(PartyProxy committer) {
        this.committer = committer;
    }

    void setAuditTime(DvDateTime auditTime) {
        this.auditTime = auditTime;
    }
    // POJO end

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditInfo)) {
            return false;
        }

        final AuditInfo ai = (AuditInfo) o;

        return new EqualsBuilder().append(systemId, ai.systemId).append(committer, ai.committer).append(auditTime,
                ai.auditTime).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(5, 23).append(systemId).append(committer).append(auditTime).toHashCode();
    }

    /* fields */
    private String systemId;
    private PartyProxy committer;
    private DvDateTime auditTime;
    
    public AuditEntry toEntry(DvCodedText auditType, DvText description, TerminologyService terminologyService) {
        return new AuditEntry(systemId, committer, auditTime, auditType, description, terminologyService);
    }
}

/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *  The Original Code is AuditDetails.java
 *
 *  The Initial Developer of the Original Code is Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2003-2004
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */
