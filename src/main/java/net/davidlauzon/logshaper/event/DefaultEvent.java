package net.davidlauzon.logshaper.event;


import net.davidlauzon.logshaper.journal.EventJournal;
import net.davidlauzon.logshaper.attribute.*;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by david on 15-11-04.
 *
 * Log levels hierarchy: ERROR, WARN, INFO, DEBUG, TRACE
 */
public class DefaultEvent implements LogEvent
{
    protected EventJournal journal;
    protected LogEvent parent;

    protected String eventName;
    protected int depth;

    protected boolean isPonctual;
    protected boolean isRelative;
    protected long eventStartedAtMS;
    protected long eventEndedAtMS;
    protected EventState state;

    protected Map<String,Attribute> attributes;


    /**
     * Constructor reserved for internal use. Instead use EventJournal's factory methods to create an event.
     *
     * @see EventJournal
     * @param journal     The journal where this event will be logged to.
     * @param name        The name of the Event.
     * @param parent      The parent event that triggered this new event.
     * @param isRelative  Tells if the ancestors are tracked relatively in the EventJournal.
     */
    public DefaultEvent(EventJournal journal, String name, LogEvent parent, boolean isRelative)
    {
        this.journal    = journal;
        this.eventName  = name;
        this.parent     = parent;
        this.depth      = (parent != null)  ?  parent.depth()+1  :  0;
        this.attributes = new LinkedHashMap<>();
        this.isRelative = isRelative;
        this.state      = EventState.NEW;
    }


    @Override
    public LogEvent newChildEvent(String name)
    {
        return new DefaultEvent( journal, name, this, this.isRelative() );
    }


    @Override
    public LogEvent newPonctualEvent(String name)
    {
        return new PonctualEvent( journal, name, this, this.isRelative() );
    }


    @Override
    public LogEvent newThrowableEvent(Throwable throwable)
    {
        return new ThrowableEvent( journal, throwable, this, this.isRelative() );
    }


    @Override
    public LogEvent count(String name, long value)
    {
        Attribute attr = attributes.get(name);
        if (attr == null)
            attributes.put( name, new LongAttribute(value) );
        else
            attr.add( value );

        // propagate the counter to the parents recursively
        if (parent != null)
            parent.count(name, value);

        return this;
    }


    @Override
    public LogEvent count(String name, double value)
    {
        Attribute attr = attributes.get(name);
        if (attr == null)
            attributes.put( name, new DoubleAttribute(value) );
        else
            attr.add( value );

        // propagate the counter to the parents recursively
        if (parent != null)
            parent.count(name, value);

        return this;
    }


    @Override
    public LogEvent attr(String name, String value)
    {
        this.attributes.put( name, new StringAttribute( value ) );

        return this;
    }

    @Override
    public LogEvent attr(String name, boolean value)
    {
        this.attributes.put( name, new BooleanAttribute( value ) );

        return this;
    }

    @Override
    public LogEvent attr(String name, long value)
    {
        this.attributes.put( name, new LongAttribute( value ) );

        return this;
    }

    @Override
    public LogEvent attr(String name, double value)
    {
        this.attributes.put( name, new DoubleAttribute( value ) );

        return this;
    }


    @Override
    public LogEvent publishTrace()
    {
        if (state == EventState.NEW)
            start();
        journal.publishTrace(this);

        return this;
    }

    @Override
    public LogEvent publishDebug()
    {
        if (state == EventState.NEW)
            start();
        journal.publishDebug(this);

        return this;
    }

    @Override
    public LogEvent publishInfo()
    {
        if (state == EventState.NEW)
            start();
        journal.publishInfo(this);

        return this;
    }

    @Override
    public LogEvent publishWarn()
    {
        if (state == EventState.NEW)
            start();
        journal.publishWarn(this);

        return this;
    }

    @Override
    public LogEvent publishError()
    {
        if (state == EventState.NEW)
            start();
        journal.publishError(this);

        return this;
    }


    @Override
    public LogEvent start()
    {
        state               = EventState.STARTED;
        eventStartedAtMS    = System.currentTimeMillis();

        journal().onEventStarted(this);

        return this;
    }

    @Override
    public LogEvent stop()
    {
        eventEndedAtMS  = System.currentTimeMillis();
        state           = EventState.ENDED;

        // Accumulating this event's duration into the parent's scope
        if (parent != null)
            parent.count(getDurationCounterName(), durationInMS());

        journal().onEventStopped(this);

        return this;
    }

    @Override
    public LogEvent stopAll()
    {
        stop();
        if (parent != null)
            parent.stopAll();

        return this;
    }

    protected String getDurationCounterName()
    {
        return this.eventName.replace(' ', '.') + ".MS";
    }


    @Override
    public Map<String,Attribute> attributes()
    {
        return attributes;
    }

    @Override
    public long durationInMS()
    {
        if (eventStartedAtMS > 0 && eventEndedAtMS > 0)
            return (eventEndedAtMS - eventStartedAtMS);
        else
            return 0;
    }

    @Override
    public LogEvent parent() {
        return parent;
    }

    @Override
    public String eventName()
    {
        return eventName;
    }

    @Override
    public EventState state()
    {
        return state;
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public boolean isPonctual() {
        return this.isPonctual;
    }

    @Override
    public boolean isRelative() {
        return this.isRelative;
    }

    protected EventJournal journal() {
        return this.journal;
    }

}
