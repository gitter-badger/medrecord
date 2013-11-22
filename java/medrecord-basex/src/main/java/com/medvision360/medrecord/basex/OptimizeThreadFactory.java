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
