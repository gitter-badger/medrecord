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
package com.medvision360.medrecord.basex;

import java.util.concurrent.ThreadFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class OptimizeThreadFactory implements ThreadFactory
{
    private String m_name;
    private int c = 1;
    private ThreadGroup m_threadGroup;

    public OptimizeThreadFactory(String name)
    {
        m_name = "optimize-" + checkNotNull(name);
        m_threadGroup = new ThreadGroup(name);
        m_threadGroup.setMaxPriority(Thread.MIN_PRIORITY + 1);
        m_threadGroup.setDaemon(true);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Thread newThread(Runnable runnable)
    {
        Thread t = new Thread(m_threadGroup, runnable, newName());
        t.setPriority(Thread.MIN_PRIORITY + 1);
        t.setDaemon(true);
        return t;
    }

    private String newName()
    {
        return m_name + "-" + c++;
    }
}
