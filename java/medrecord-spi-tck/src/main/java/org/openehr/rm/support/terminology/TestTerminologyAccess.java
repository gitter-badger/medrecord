/*
 * component:   "openEHR Reference Implementation"
 * description: "Class TestTerminologyAccess"
 * keywords:    "unit test"
 *
 * author:      "Rong Chen <rong@acode.se>"
 * support:     "Acode HB <support@acode.se>"
 * copyright:   "Copyright (c) 2004 Acode HB, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL: http://svn.openehr.org/ref_impl_java/BRANCHES/Release-1.0/libraries/src/test/org/openehr/rm/support/terminology/TestTerminologyAccess.java $"
 * revision:    "$LastChangedRevision: 2 $"
 * last_change: "$LastChangedDate: 2005-10-12 22:20:08 +0100 (Wed, 12 Oct 2005) $"
 */
package org.openehr.rm.support.terminology;

import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.identification.TestTerminologyID;

import java.util.HashSet;
import java.util.Set;

/**
 * TestTerminologyAccess
 * 
 * @author Rong Chen
 * @version 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "Convert2Diamond"})
public class TestTerminologyAccess implements TerminologyAccess {

    public Set<CodePhrase> codesForGroupId(String groupID) {
        return null;
    }

    public Set<CodePhrase> codesForGroupName(String name, String language) {
        return CODES;
    }

    public String rubricForCode(String code, String language) {
        return null;
    }

    public boolean hasCodeForGroupName(CodePhrase code, String name,
                                       String language) {
        return true;
    }

    public String id() {
        return null;
    }

    public Set<CodePhrase> allCodes() {
        return null;
    }

    public boolean has(CodePhrase code) {
        return true;
    }
    
    public boolean hasCodeForGroupId(String groupId, CodePhrase code) {
		return true;
	}
    
    public static final CodePhrase RELATIONS = new CodePhrase("test", "family_code");
    public static final CodePhrase SETTING = new CodePhrase("test", "setting_code");
    public static final CodePhrase FUNCTION = new CodePhrase(TestTerminologyID.SNOMEDCT, "meanCode");
    public static final CodePhrase REVISION = new CodePhrase(TestTerminologyID.SNOMEDCT, "revisionCode");
    public static final CodePhrase CHANGE = new CodePhrase(TestTerminologyID.SNOMEDCT, 
            "changeTypeCode");
    public static final CodePhrase ACTIVE = new CodePhrase("test", "active");
    public static final CodePhrase CREATION = new CodePhrase("openehr", "249");
    public static final CodePhrase PERSISTENT = new CodePhrase("test", "persistent");
    public static final CodePhrase EVENT = new CodePhrase("test", "event");
    public static final CodePhrase ENGLISH = new CodePhrase("test", "en");
    public static final CodePhrase LATIN_1 = new CodePhrase("test",
            "iso-8859-1");
    public static final CodePhrase NULL_FLAVOUR = new CodePhrase("test",
            "unanswered");
	public static final CodePhrase SOME_STATE = null;
	public static final CodePhrase SOME_TRANSITION = null;
    
    
    static Set<CodePhrase> CODES; 
    static {
        CODES = new HashSet<CodePhrase>();
        CODES.add(FUNCTION);
        CODES.add(REVISION);
        CODES.add(EVENT);
        CODES.add(PERSISTENT);
        CODES.add(SETTING);
        CODES.add(CHANGE);
        CODES.add(RELATIONS);
        CODES.add(CREATION);
        CODES.add(ENGLISH); 
        CODES.add(ACTIVE);
        CODES.add(NULL_FLAVOUR);
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
 *  The Original Code is TestTerminologyAccess.java
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
