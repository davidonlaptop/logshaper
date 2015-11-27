package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.journal.EventJournal;

/**
 * Created by david on 15-11-26.
 */
public class PonctualEvent extends DefaultEvent
{
    public PonctualEvent( EventJournal journal, String name, LogEvent parent, boolean isRelative )
    {
        super(journal, name, parent, isRelative);

        this.isPonctual         = true;
        this.eventStartedAtMS   = System.currentTimeMillis();
        this.eventEndedAtMS     = this.eventStartedAtMS;
        this.state              = EventState.ENDED;

        journal().onPonctualEvent(this);
    }
}
