/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
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
import org.openehr.rm.common.generic.AuditDetails;
import org.openehr.rm.common.generic.PartyProxy;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * This is a variant of {@link AuditDetails} that has an auditType rather than a changeType, and so can properly be
 * used to describe audit log entries that are not actually changes (for example, it can describe <em>access</em>). 
 * Use {@link #toDetails()} to convert to an equivalent {@link AuditDetails} where possible.
 *
 * @author Rong Chen
 * @author Leo Simons
 */
public class AuditEntry extends RMObject {
    private static final long serialVersionUID = 0x130L; 
    
    private transient TerminologyService terminologyService;

    @FullConstructor
    public AuditEntry(@Attribute(name = "systemId", required = true) String systemId,
                      @Attribute(name = "committer", required = true) PartyProxy committer,
                      @Attribute(name = "auditTime", required = true) DvDateTime auditTime,
                      @Attribute(name = "auditType", required = true) DvCodedText auditType,
                      @Attribute(name = "description") DvText description,
                      @Attribute(name = "terminologyService", system = true) TerminologyService terminologyService) {
        if (StringUtils.isEmpty(systemId)) {
            throw new IllegalArgumentException("empty systemId");
        }
        if (committer == null) {
            throw new IllegalArgumentException("null committer");
        }
        if (auditTime == null) {
            throw new IllegalArgumentException("null auditTime");
        }
        if (auditType == null) {
            throw new IllegalArgumentException("null auditType");
        }
        if (terminologyService == null) {
            throw new IllegalArgumentException("null terminologyService");
        }
        if (!terminologyService.terminology("medrecord") //TerminologyService.OPENEHR)
                .codesForGroupName("audit type", "en").contains(auditType.getDefiningCode())) {
            throw new IllegalArgumentException("unknown change type: " + auditType.getDefiningCode());
        }
        this.systemId = systemId;
        this.committer = committer;
        this.auditTime = auditTime;
        this.auditType = auditType;
        this.description = description;
        this.terminologyService = terminologyService;
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

    public DvCodedText getAuditType() {
        return auditType;
    }

    public DvText getDescription() {
        return description;
    }

    // POJO start
    protected AuditEntry() {
    }

    void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    void setAuditType(DvCodedText auditType) {
        this.auditType = auditType;
    }

    void setDescription(DvText description) {
        this.description = description;
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
        if (!(o instanceof AuditEntry)) {
            return false;
        }

        final AuditEntry ai = (AuditEntry) o;

        return new EqualsBuilder().append(systemId, ai.systemId).append(committer, ai.committer).append(auditTime,
                ai.auditTime).append(auditType, ai.auditType).append(description, ai.description).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(5, 23).append(systemId).append(committer).append(auditTime).append(auditType).append(
                description).toHashCode();
    }

    /* fields */
    private String systemId;
    private PartyProxy committer;
    private DvDateTime auditTime;
    private DvCodedText auditType;
    private DvText description;
    
    public AuditDetails toDetails() {
        return new AuditDetails(systemId, committer, auditTime, getChangeType(), description, terminologyService);
    }
    
    private DvCodedText getChangeType() {
        CodePhrase definingCode = new CodePhrase(new TerminologyID(TerminologyService.OPENEHR), auditType.getCode());
        
        DvCodedText result = new DvCodedText(
                auditType.getValue(),
                null, // todo get the term mapping for audit type 
                auditType.getFormatting(),
                auditType.getHyperlink(),
                auditType.getLanguage(),
                auditType.getEncoding(),
                definingCode,
                terminologyService
        );
        
        return result;
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
