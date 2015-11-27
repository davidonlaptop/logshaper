package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.EventJournal;

/**
 * Created by david on 15-11-26.
 */
public class PonctualEvent extends DefaultEvent
{
    public PonctualEvent( EventJournal journal, String name, int depth, LogEvent parent, boolean isRelative )
    {
        super(journal, name, depth, parent, isRelative);

        this.isPonctual         = true;
        this.eventStartedAtMS   = System.currentTimeMillis();
        this.eventEndedAtMS     = this.eventStartedAtMS;
        this.state              = EventState.ENDED;
    }
}
