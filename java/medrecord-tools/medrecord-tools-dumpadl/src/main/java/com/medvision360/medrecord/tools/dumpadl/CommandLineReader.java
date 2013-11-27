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
package com.medvision360.medrecord.tools.dumpadl;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Simple class wrapping the command line parsing stuff.
 */
public final class CommandLineReader
{
    /**
     * Object holding the command line options.
     */
    private final Options m_options;

    /**
     * The parsed commandline options.
     */
    private CommandLine m_commandLine;

    /**
     * Constructor.
     * <p/>
     * Creates the commandline options.
     */
    public CommandLineReader()
    {
        super();

        m_options = new Options();

        final Option adlOption = new Option("i", "adl", true, "The ADL file to parse.");
        adlOption.setRequired(true);
        adlOption.setArgName("adl-file");
        m_options.addOption(adlOption);
    }

    /**
     * Gets the name of the ADL file to load.
     *
     * @return The ADL file to load.
     */
    public String getAdlFile()
    {
        return m_commandLine.getOptionValue('i');
    }

    /**
     * Parse the command line options.
     *
     * @param args The command line arguments.
     */
    public void parse(final String[] args) throws ParseException
    {
        final BasicParser parser = new BasicParser();
        m_commandLine = parser.parse(m_options, args);
    }

    /**
     * Print an overview of the command line options.
     */
    public void printHelp()
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("xxx [options]", m_options);
    }
}
