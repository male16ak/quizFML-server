package server.utility;

import server.Controllers.Config;
import server.utility.Log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;

public class Globals implements ServletContextListener {

    public static Log log = new Log();

    public static Config config = new Config();

    /**
     * Denne funktion kan blive brugt til at instantiere Logger og Config klasserne.
     *
     * Vi gør dette for at resten af programmet kan bruge det efterfølgende.
     *
     * Denne metode bliver automatisk kaldt af Jersey når serveren starter.
     *
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //We init config in order to read the file and set all the variables.
        //Since we're not sure the file exists, we could get a IOException
        try {
            config.initConfig();
        } catch (IOException e) {
            log.writeLog(this.getClass().getName(), this, "We are now including the config file", 2);

            e.printStackTrace();
        }

        //Here we can initialize our Logger class and write to our Logging.txt that the system has been started
        System.out.println("Context is initialized");
        log.writeLog(this.getClass().getName(), this, "We've started the system", 2);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //Logging for when the system is stopped
        System.out.println("Context is destroyed");
        log.writeLog(this.getClass().getName(), this, "The system has been stopped", 2);
    }
}
