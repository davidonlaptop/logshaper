package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.LogEvent;

/**
 * Created by david on 15-11-06.
 */
public class LogShaper
{
    static private final EventJournal defaultJournal = new EventJournal();


    static public final EventJournal getDefaultJournal() {
        return defaultJournal;
    }


    /**
     * Creates a new root event in the default journal
     *
     * @param name The name of the event
     * @return the event
     */
    static public LogEvent newRootEvent(String name)
    {
        return getDefaultJournal().newRootEvent(name);
    }
}
