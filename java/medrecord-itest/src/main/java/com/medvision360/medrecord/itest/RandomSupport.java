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
package com.medvision360.medrecord.itest;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple utility to do some things randomly.
 */
public class RandomSupport
{
    private final static Log log = LogFactory.getLog(LocatableGenerator.class);
    private Random m_random = new Random();

    public <T> T pick(T[] options)
    {
        if (options == null)
        {
            log.warn("Nothing to pick from");
            return null;
        }
        int length = options.length;
        if (length == 0)
        {
            log.warn("Empty array to pick from");
            return null;
        }
        if (length == 1)
        {
            return options[0];
        }

        int pick = m_random.nextInt(options.length);
        return options[pick];
    }

    public <T> T pick(Iterable<T> options)
    {
        if (options == null)
        {
            return null;
        }
        List<T> optionList = Lists.newArrayList(options);
        if (optionList.isEmpty())
        {
            return null;
        }
        if (optionList.size() == 1)
        {
            return optionList.get(0);
        }

        int pick = m_random.nextInt(optionList.size());
        return optionList.get(pick);
    }

    public boolean should(@SuppressWarnings("UnusedParameters") double probability)
    {
        return true;
        //return m_random.nextDouble() < probability;
    }

    //public boolean should()
    //{
    //    return true;
    //    //return m_random.nextBoolean();
    //}

    public int listSize(int maxSize)
    {
        maxSize = Math.max(1, maxSize);
        if (maxSize == 1)
        {
            return 1;
        }
        return 1 + m_random.nextInt(maxSize - 1);
    }

    public int chooseOccurrences(boolean unique, int lower, int upper)
    {
        int occurrences;
        if (upper == 0)
        {
            occurrences = 0;
        }
        else if (unique && lower <= 1 && upper >= 1)
        {
            occurrences = 1;
        }
        else
        {
            occurrences = listSize(upper);
        }
        return occurrences;
    }
}
