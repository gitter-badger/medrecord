package com.medvision360.medrecord.server;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.medvision360.lib.server.config.ConfigurationException;
import com.medvision360.lib.server.config.ConfigurationLoader;
import com.medvision360.lib.server.config.ConfigurationWrapper;
import com.medvision360.lib.server.config.ConfigureLogger;
import com.medvision360.lib.server.config.ServerProperties;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The medvision360 serverlib loads its logging configuration during restlet startup. That's too late for the 
 * middleware app, since <i>it</i> will load spring (which does logging) before restlet is initialized.
 * 
 * Because listeners are set up before servlets, using this listener ensures we get to the log configuration
 * early on in the webapp startup cycle.
 */
public class LoggingConfigurationListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        tryToPreLoadLoggingConfig();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }

    public void tryToPreLoadLoggingConfig()
    {
        String file = System.getProperty(ConfigurationLoader.SYSTEM_PROPERTY_CONFIGFILE);
        if (file == null)
        {
            try
            {
                file = ServerProperties.loadFromWar("WEB-INF/com.medvision360.lib.server.properties", "configfile");
            }
            catch (IOException e)
            {
                return;
            }
        }

        if (file == null)
        {
            return;
        }

        Logger logger = LoggerFactory.getLogger(LoggingConfigurationListener.class);
        PropertiesConfiguration config;
        try
        {
            config = new PropertiesConfiguration(file);
        }
        catch (org.apache.commons.configuration.ConfigurationException e)
        {
            return;
        }
        config.setThrowExceptionOnMissing(true);

        ConfigurationWrapper wrapper = new ConfigurationWrapper(logger, config);
        try
        {
            ConfigureLogger.setupLogging(logger, wrapper);
        }
        catch (ConfigurationException e)
        {
            return;
        }
    }
}
