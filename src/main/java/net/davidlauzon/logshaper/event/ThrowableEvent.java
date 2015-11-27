package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.journal.EventJournal;

/**
 * Created by david on 15-11-22.
 */
public class ThrowableEvent extends PonctualEvent
{
    static private final String DEFAULT_EVENT_NAME = "Exception";

    private Throwable throwable;


    /**
     * Constructor reserved for internal use.
     *
     * @param journal    The journal where this event will be logged to
     * @param throwable  The throwable exception or error to be thrown
     * @param parent     The parent event that triggered this new event
     * @param isRelative If this event ancestors are tracked relatively (see @EventJournal)
     */
    public ThrowableEvent( EventJournal journal, Throwable throwable, LogEvent parent, boolean isRelative )
    {
        // Create a new ponctual event
        super( journal, DEFAULT_EVENT_NAME, parent, isRelative );

        this.throwable = throwable;
    }


    @Override
    public LogEvent publishTrace()
    {
        journal().publishTrace( this, throwable );

        return this;
    }

    @Override
    public LogEvent publishDebug()
    {
        journal().publishDebug( this, throwable );

        return this;
    }

    @Override
    public LogEvent publishInfo()
    {
        journal().publishInfo( this, throwable );

        return this;
    }

    @Override
    public LogEvent publishWarn()
    {
        journal().publishWarn( this, throwable );

        return this;
    }

    @Override
    public LogEvent publishError()
    {
        journal().publishError( this, throwable );

        return this;
    }
}
