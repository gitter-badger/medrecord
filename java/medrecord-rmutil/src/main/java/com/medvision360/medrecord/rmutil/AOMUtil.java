package com.medvision360.medrecord.rmutil;

import java.util.List;
import java.util.Map;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.ArchetypeConstraint;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.ConstraintRef;
import org.openehr.am.archetype.ontology.ArchetypeOntology;
import org.openehr.am.archetype.ontology.OntologyBinding;
import org.openehr.am.archetype.ontology.OntologyBindingItem;
import org.openehr.am.archetype.ontology.TermBindingItem;
import org.openehr.build.SystemValue;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AOMUtil extends RMUtil
{
    private static final Logger log = LoggerFactory.getLogger(RMUtil.class);

    public AOMUtil(Map<SystemValue, Object> systemValues)
    {
        super(systemValues);
    }

    public AOMUtil()
    {
        super();
    }

    protected String name(ArchetypeConstraint constraint)
    {
        if (constraint == null)
        {
            return null;
        }
        if (constraint instanceof CObject)
        {
            CObject object = (CObject) constraint;
            return object.getRmTypeName();
        }
        else if (constraint instanceof CAttribute)
        {
            CAttribute attribute = (CAttribute) constraint;
            return attribute.getRmAttributeName();
        }
        return constraint.getClass().getSimpleName();
    }

    protected Object translateConstraintRef(Archetype archetype, ConstraintRef ref)
    {
        String reference = ref.getReference();
        ArchetypeOntology ontology = archetype.getOntology();
        //String primaryLanguage = ontology.getPrimaryLanguage();
        //ArchetypeTerm term = ontology.constraintDefinition(primaryLanguage, reference);
        List<OntologyBinding> termBindings = ontology.getTermBindingList();
        for (OntologyBinding termBinding : termBindings)
        {
            List<OntologyBindingItem> bindingItemList = termBinding.getBindingList();
            for (OntologyBindingItem ontologyBindingItem : bindingItemList)
            {
                if (!(ontologyBindingItem instanceof TermBindingItem))
                {
                    continue;
                }
                TermBindingItem termBindingItem = (TermBindingItem) ontologyBindingItem;
                String termCode = termBindingItem.getCode();
                if (termCode.equals(reference))
                {
                    List<String> terms = termBindingItem.getTerms();
                    for (String term : terms)
                    {
                        term = term.trim();
                        if (term.startsWith("[") || term.startsWith("<"))
                        {
                            term = term.substring(1);
                        }
                        if (term.endsWith("]") || term.endsWith(">"))
                        {
                            term = term.substring(0, term.length() - 1);
                        }
                        CodePhrase phrase;
                        try
                        {
                            phrase = (CodePhrase) CodePhrase.parseValue(term);
                            log.debug(String.format("Mapped ref %s to %s", reference, phrase));
                            return phrase;
                        }
                        catch (IllegalArgumentException | ClassCastException e)
                        {
                            log.warn(String.format("Could not parse term binding %s, skipping it", term));
                            continue;
                        }
                    }
                }
            }
        }
        log.debug(String.format("Returning ref %s, no term binding done", reference));
        return reference;
    }
}
