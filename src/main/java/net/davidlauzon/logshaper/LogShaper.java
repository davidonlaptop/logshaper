package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.LogEvent;
import net.davidlauzon.logshaper.journal.EventJournal;
import net.davidlauzon.logshaper.journal.SimpleJournal;

/**
 * Created by david on 15-11-06.
 */
public class LogShaper
{
    static private EventJournal defaultJournal = new SimpleJournal();


    static public final EventJournal defaultJournal() {
        return defaultJournal;
    }


    static public final void setDefaultJournal( EventJournal journal )
    {
        defaultJournal = journal;
    }
}
