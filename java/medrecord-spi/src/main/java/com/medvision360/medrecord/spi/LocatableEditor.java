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
package com.medvision360.medrecord.spi;

import java.net.URI;

import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.directory.Folder;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.composition.content.navigation.Section;
import org.openehr.rm.demographic.Actor;
import org.openehr.rm.demographic.Contact;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.demographic.PartyRelationship;
import org.openehr.rm.demographic.Role;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.UIDBasedID;

/**
 * Convenience API for making changes to RM objects. Does not persist changes.
 */
public interface LocatableEditor
{
    ///
    /// Base
    ///
    
    public void set(Pathable pathable, String path, Object value);
    
    public void addLink(Locatable locatable, String meaning, String type, URI target);

    ///
    /// Structures
    ///
    
    public void addFolder(Folder folder, Folder subFolder);
    
    public void addFolder(Folder folder, UIDBasedID subFolder);
    
    public void removeFolder(Folder folder, Folder subFolder);
    
    public void removeFolder(Folder folder, UIDBasedID subFolder);
    
    public void addItem(Folder folder, Locatable item);
    
    public void addItem(Folder folder, ObjectRef item);
    
    public void removeItem(Folder folder, Locatable item);
    
    public void removeItem(Folder folder, ObjectRef item);
    
    ///
    /// EHR
    ///
    
    public void addItem(Composition composition, ContentItem contentItem);
    
    public void addItem(Section section, ContentItem contentItem);
    
    ///
    /// Demographic
    ///
    
    public void addIdentity(Actor actor, PartyIdentity identity);
    
    public void removeIdentity(Actor actor, PartyIdentity identity);
    
    public void removeIdentity(Actor actor, UIDBasedID identity);
    
    public void addContact(Actor actor, Contact contact);
    
    public void removeContact(Actor actor, Contact contact);
    
    public void removeContact(Actor actor, UIDBasedID contact);
    
    public void addRelationship(Actor actor, PartyRelationship relationship);
    
    public void removeRelationship(Actor actor, PartyRelationship relationship);
    
    public void removeRelationship(Actor actor, UIDBasedID relationship);
    
    public void addRole(Actor actor, Role role);

    public void removeRole(Actor actor, Role role);

    public void removeRole(Actor actor, UIDBasedID role);
}
