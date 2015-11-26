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
     * Create a new root event in the default registry
     * @param name The name of the event
     * @return the event
     */
    static public LogEvent createRootEvent( String name )
    {
        return getDefaultJournal().createRootEvent( name );
    }
}
