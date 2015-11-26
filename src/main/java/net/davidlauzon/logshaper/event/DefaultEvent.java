package net.davidlauzon.logshaper.event;


import net.davidlauzon.logshaper.EventJournal;
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
    private EventJournal journal;
    private LogEvent parent;

    private String eventName;
    private int depth;

    private boolean isPonctual;
    private long eventStartedAtMS;
    private long eventEndedAtMS;
    private EventState state;

    private Map<String,Attribute> attributes;


    /**
     * (Constructor reserved for internal use). See @EventJournal for how to initialize the root event
     *
     * @param journal   The journal where this event will be logged to
     * @param parent    The parent event that triggered this new event.
     * @param depth     The level of depth from the root event (0 if no parent)
     * @param name      The name of the Event
     */
    public DefaultEvent(EventJournal journal, String name, int depth, LogEvent parent)
    {
        this.journal    = journal;
        this.eventName  = name;
        this.depth      = depth;
        this.parent     = parent;
        this.attributes = new LinkedHashMap<>();
        state           = EventState.NEW;
    }

    /**
     * For internal used only.
     *
     * @param journal
     * @param name
     * @param depth
     * @param parent
     * @param isPonctual
     */
    protected DefaultEvent(EventJournal journal, String name, int depth, LogEvent parent, boolean isPonctual)
    {
        this.journal    = journal;
        this.eventName  = name;
        this.depth      = depth;
        this.parent     = parent;
        this.attributes = new LinkedHashMap<>();

        if (isPonctual) {
            this.isPonctual = true;
            this.state = EventState.ENDED;
            this.eventStartedAtMS = System.currentTimeMillis();
            this.eventEndedAtMS = this.eventStartedAtMS;
        }
    }


    @Override
    public LogEvent newChildEvent(String name)
    {
        return new DefaultEvent( journal, name, depth + 1, this );
    }


    @Override
    public LogEvent newPonctualEvent(String name)
    {
        return new DefaultEvent( journal, name, depth + 1, this, true);
    }


    @Override
    public LogEvent newThrowableEvent(Throwable throwable)
    {
        return new ThrowableEvent( journal, depth + 1, this, throwable );
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

    protected EventJournal journal() {
        return this.journal;
    }

}
