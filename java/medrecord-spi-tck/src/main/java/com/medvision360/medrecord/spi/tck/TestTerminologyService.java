/*
 * component:   "openEHR Reference Implementation"
 * description: "Class TestTerminologyService"
 * keywords:    "unit test"
 *
 * author:      "Rong Chen <rong@acode.se>"
 * support:     "Acode HB <support@acode.se>"
 * copyright:   "Copyright (c) 2004 Acode HB, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL: http://svn.openehr.org/ref_impl_java/BRANCHES/RM-1.0-update/libraries/src/test/org/openehr/rm/support/terminology/TestTerminologyService.java $"
 * revision:    "$LastChangedRevision: 50 $"
 * last_change: "$LastChangedDate: 2006-08-10 13:21:46 +0200 (Thu, 10 Aug 2006) $"
 */
package com.medvision360.medrecord.spi.tck;

import java.util.List;
import java.util.Map;

import org.openehr.rm.support.terminology.CodeSetAccess;
import org.openehr.rm.support.terminology.OpenEHRCodeSetIdentifiers;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * TestTerminologyService
 *
 * @author Rong Chen
 * @version 1.0
 */
public class TestTerminologyService implements TerminologyService
{

    public static TestTerminologyService getInstance()
    {
        return new TestTerminologyService();
    }

    public TerminologyAccess terminology(String name)
    {
        return new TestTerminologyAccess();
    }

    public CodeSetAccess codeSet(String name)
    {
        return new TestCodeSetAccess();
    }

    public boolean hasTerminology(String name)
    {
        return false;
    }

    public boolean hasCodeSet(String name)
    {
        return false;
    }


    public CodeSetAccess codeSetForId(OpenEHRCodeSetIdentifiers name)
    {
        return new TestCodeSetAccess();
    }


    public List<String> terminologyIdentifiers()
    {
        return null;
    }


    public List<String> codeSetIdentifiers()
    {
        return null;
    }


    public Map<String, String> openehrCodeSets()
    {
        return null;
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
 *  The Original Code is TestTerminologyService.java
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
