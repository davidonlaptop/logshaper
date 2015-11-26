package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.EventJournal;

/**
 * Created by david on 15-11-22.
 */
public class ThrowableEvent extends DefaultEvent
{
    private Throwable throwable;


    /**
     * (Constructor reserved for internal use). See @EventJournal for how to initialize the root event
     *
     * @param journal   The journal where this event will be logged to
     * @param parent    The parent event that triggered this new event.
     * @param depth     The level of depth from the root event (0 if no parent)
     * @param name      The name of the LogEvent
     * @param throwable The throwable exception or error to be thrown
     */
    public ThrowableEvent(EventJournal journal, String name, int depth, LogEvent parent, Throwable throwable)
    {
        super( journal, name, depth, parent );

        this.throwable = throwable;
        this.ponctualEvent();
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
