package com.medvision360.medrecord.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseXConfigurationListener implements ServletContextListener
{
    private final static Logger log = LoggerFactory.getLogger(BaseXConfigurationListener.class);
    private final static String CONFIG_FILE = "/etc/medvision360%s/basex.properties";
    
    public void contextInitialized(ServletContextEvent sce)
    {
        String contextPath = sce.getServletContext().getContextPath();
        String configFileName = String.format(CONFIG_FILE, contextPath);

        File configFile = new File(configFileName);
        if (configFile.exists())
        {
            log.info("Loading BaseX config from {}", configFileName);
            
            try
            {
                FileInputStream fis = new FileInputStream(configFile);
                Properties p = new Properties();
                p.load(fis);
                String dbPath = p.getProperty("basex.dbPath");
                if (dbPath != null)
                {
                    File dbDirectory = new File(dbPath);
                    if (!dbDirectory.exists())
                    {
                        dbDirectory.mkdirs();
                    }

                    //noinspection SpellCheckingInspection
                    System.setProperty("org.basex.DBPATH", dbPath);
                    //noinspection SpellCheckingInspection
                    log.info("BaseX DBPATH {}", dbPath);
                }
                else
                {
                    log.info("basex.dbPath not set, using default");
                }
                String repoPath = p.getProperty("basex.repoPath");
                if (repoPath != null)
                {
                    File dbDirectory = new File(dbPath);
                    if (!dbDirectory.exists())
                    {
                        dbDirectory.mkdirs();
                    }

                    //noinspection SpellCheckingInspection
                    System.setProperty("org.basex.REPOPATH", repoPath);
                    //noinspection SpellCheckingInspection
                    log.info("BaseX REPOPATH {}", repoPath);
                }
                else
                {
                    log.info("basex.repoPath not set, using default");
                }
            }
            catch (IOException e)
            {
                String message = String.format("Error reading %s: %s", CONFIG_FILE, e.getMessage());
                log.error(message, e);
                throw new IllegalStateException(message, e);
            }
        }
        else
        {
            log.info("No config file at {}, basex will be using defaults", configFileName);
        }
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
