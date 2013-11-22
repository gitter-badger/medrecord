package com.zorggemak.servlet;

import com.zorggemak.data.DataManager;
import com.zorggemak.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServletListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ServletListener.class);
    private static final long DATA_LOOP = 60 * 1000;

    private static Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Configuration.init();

            if (timer == null) {
                timer = new Timer();
                timer.schedule(new DataCheckTask(), 0, DATA_LOOP);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private class DataCheckTask extends TimerTask {
        public DataCheckTask() {
        }

        @Override
        public void run() {
            if (log.isTraceEnabled()) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                log.trace("DataCheckTask: " + format.format(new Date()));
            }
            DataManager.getInstance().loop();
        }

    }

}
