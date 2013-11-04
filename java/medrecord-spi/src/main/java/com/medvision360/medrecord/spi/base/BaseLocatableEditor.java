package com.medvision360.medrecord.spi.base;

import java.net.URI;

import com.medvision360.medrecord.spi.LocatableEditor;
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

public class BaseLocatableEditor implements LocatableEditor
{
    @Override
    public void set(Pathable pathable, String path, Object value)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.set()");
    }

    @Override
    public void addLink(Locatable locatable, String meaning, String type, URI target)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addLink()");
    }

    @Override
    public void addFolder(Folder folder, Folder subFolder)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addFolder()");
    }

    @Override
    public void addFolder(Folder folder, UIDBasedID subFolder)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addFolder()");
    }

    @Override
    public void removeFolder(Folder folder, Folder subFolder)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeFolder()");
    }

    @Override
    public void removeFolder(Folder folder, UIDBasedID subFolder)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeFolder()");
    }

    @Override
    public void addItem(Folder folder, Locatable item)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addItem()");
    }

    @Override
    public void addItem(Folder folder, ObjectRef item)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addItem()");
    }

    @Override
    public void removeItem(Folder folder, Locatable item)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeItem()");
    }

    @Override
    public void removeItem(Folder folder, ObjectRef item)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeItem()");
    }

    @Override
    public void addItem(Composition composition, ContentItem contentItem)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addItem()");
    }

    @Override
    public void addItem(Section section, ContentItem contentItem)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addItem()");
    }

    @Override
    public void addIdentity(Actor actor, PartyIdentity identity)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addIdentity()");
    }

    @Override
    public void removeIdentity(Actor actor, PartyIdentity identity)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeIdentity()");
    }

    @Override
    public void removeIdentity(Actor actor, UIDBasedID identity)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeIdentity()");
    }

    @Override
    public void addContact(Actor actor, Contact contact)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addContact()");
    }

    @Override
    public void removeContact(Actor actor, Contact contact)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeContact()");
    }

    @Override
    public void removeContact(Actor actor, UIDBasedID contact)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeContact()");
    }

    @Override
    public void addRelationship(Actor actor, PartyRelationship relationship)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addRelationship()");
    }

    @Override
    public void removeRelationship(Actor actor, PartyRelationship relationship)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeRelationship()");
    }

    @Override
    public void removeRelationship(Actor actor, UIDBasedID relationship)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeRelationship()");
    }

    @Override
    public void addRole(Actor actor, Role role)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.addRole()");
    }

    @Override
    public void removeRole(Actor actor, Role role)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeRole()");
    }

    @Override
    public void removeRole(Actor actor, UIDBasedID role)
    {
        throw new UnsupportedOperationException("todo implement BaseLocatableEditor.removeRole()");
    }
}
