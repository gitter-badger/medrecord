/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.tools.dumpadl;

import java.io.File;
import java.util.List;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.acode.openehr.parser.ADLParser;

/**
 * Application class for the Java Client Generator.
 */
public final class Dumper
{
    /**
     * Object used for logging.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Dumper.class);

    /**
     * Hidden constructor.
     */
    private Dumper()
    {
        super();
    }

    /**
     * Program entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args)
    {
        LOG.info("MEDrecord ADL Dumper starting...");
        final CommandLineReader commandLineReader = new CommandLineReader();
        try
        {
            commandLineReader.parse(args);

            final File file = new File(commandLineReader.getAdlFile());
            final ADLParser parser = new ADLParser(file);
            final Archetype adl = parser.parse();

            dump(adl, "", "definition", adl.getDefinition());

        }
        catch(Exception e)
        {
            LOG.error("Error: {}", e.getMessage(), e);
            commandLineReader.printHelp();
            System.exit(1);
        }
    }

    private static void dump(final Archetype adl, final String i, final String name, CObject object)
    {
        final ArchetypeTerm term = adl.getOntology().termDefinition("en", object.getNodeId());
        String termTxt = "";
        if (term != null)
        {
            termTxt = term.getText();
        }

        final String ident = i + "  ";
        System.out.println(String.format(
                "%-50s %-20s typeName:%-15s %6s %8s %-80s %s",
                i + name,
                object.getClass().getSimpleName(),
                object.getRmTypeName(),
                object.getNodeId(),
                occurrences(object),
                object.path(),
                termTxt
        ));

        if (object instanceof CComplexObject)
        {
            CComplexObject cobject = (CComplexObject)object;
            final List<CAttribute> attributes = cobject.getAttributes();
            for(int idx = 0;idx<attributes.size();++idx)
            {
                final CAttribute attribute = attributes.get(idx);

                dump(adl, ident, String.format("attribute[%d]", idx), attribute);
            }
        }
    }

    private static String occurrences(CObject object)
    {
        // todo object.getOccurrences().includeLower()/includeUpper()

        Integer lower = object.getOccurrences().getLower();
        Integer upper = object.getOccurrences().getUpper();

        if (upper == null)
        {
            return lower.toString() + "..*";
        }

        if (lower.equals(upper))
        {
            return lower.toString();
        }

        return lower.toString() + ".." + upper.toString();
    }

    private static void dump(Archetype adl, String i, String name, CAttribute attribute)
    {
        final String ident = i + "  ";

        System.out.println(String.format(
                "%-50s %-20s attrName:%-15s        %8s %-80s",
                i + name,
                attribute.getClass().getSimpleName(),
                attribute.getRmAttributeName(),
                occurrences(attribute),
                attribute.path()
        ));

        final List<CObject> objects = attribute.getChildren();
        for (int idx = 0; idx < objects.size(); ++idx)
        {
            final CObject object = objects.get(idx);

            dump(adl, ident, String.format("children[%d]", idx), object);
        }
    }

    private static String occurrences(CAttribute attribute)
    {
        if (attribute.isRequired())
        {
            return "1";
        }
        if (attribute.isAllowed())
        {
            return "0..1";
        }
        return "0";
    }
}

