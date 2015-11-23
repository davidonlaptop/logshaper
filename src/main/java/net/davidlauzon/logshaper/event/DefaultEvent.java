package net.davidlauzon.logshaper.event;


import net.davidlauzon.logshaper.EventJournal;
import net.davidlauzon.logshaper.attribute.Attribute;
import net.davidlauzon.logshaper.attribute.CounterAttribute;
import net.davidlauzon.logshaper.attribute.TextAttribute;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by david on 15-11-04.
 *
 * Log levels hierarchy: FATAL, ERROR, WARN, INFO, DEBUG, TRACE
 */
public class DefaultEvent implements Event
{
    private EventJournal journal;
    private Event parent;

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
    public DefaultEvent(EventJournal journal, String name, int depth, Event parent)
    {
        this.journal    = journal;
        this.eventName  = name;
        this.depth      = depth;
        this.parent     = parent;
        this.attributes = new LinkedHashMap<>();
        state           = EventState.NEW;
    }


    /**
     * Starts a new child event of the current event
     *
     * @param name The name of the event
     * @return the event newly created
     */
    @Override
    public Event createChild(String name)
    {
        return new DefaultEvent( journal, name, depth + 1, this );
    }


    /**
     * Starts a new child event of the current event
     *
     * @param name The name of the event
     * @return the event newly created
     */
    @Override
    public Event createChild( String name, Throwable throwable )
    {
        return new ThrowableEvent( journal, name, depth + 1, this, throwable );
    }


    /**
     * Creates or updates a counter with the given name and adds it the specified value.
     *
     * The Counter is automatically propagated recursively to all the parents.
     *
     * To decrement the counter, just send a negative value.
     *
     * Exemples: "DB.Duration", "Alfresco.Duration", "BIRT.Duration", "JSON.Parsing.Duration", "ComputingBudget.Duration"
     *
     * @param name          The name of the counter.
     * @param value         The value to add to the counter.
     * @return Event    The current counter.
     */
    @Override
    public Event count(String name, long value)
    {
        CounterAttribute attr = ((CounterAttribute) attributes.get(name));
        if (attr == null)
            attributes.put( name, new CounterAttribute(value) );
        else
            attr.increment(value);

        // propagate the counter to the parents recursively
        if (parent != null)
            parent.count(name, value);

        return this;
    }


    /**
     * Sets an attribute of this event.
     *
     * The scope is always local to the event (e.g. is it NOT propagated to the parents like the counters are).
     *
     * Exemples: User, SessionId, IP, UserAction, URL, Method, UserAgent
     *
     * @param name          The attribute name
     * @param value         The attribute value
     * @return Event    this event
     */
    @Override
    public Event attr(String name, String value)
    {
        this.attributes.put( name, new TextAttribute(value) );

        return this;
    }


    @Override
    public Event publishTrace()
    {
        if (state == EventState.NEW)
            start();
        journal.publishTrace(this);

        return this;
    }

    @Override
    public Event publishDebug()
    {
        if (state == EventState.NEW)
            start();
        journal.publishDebug(this);

        return this;
    }

    @Override
    public Event publishInfo()
    {
        if (state == EventState.NEW)
            start();
        journal.publishInfo(this);

        return this;
    }

    @Override
    public Event publishWarn()
    {
        if (state == EventState.NEW)
            start();
        journal.publishWarn(this);

        return this;
    }

    @Override
    public Event publishError()
    {
        if (state == EventState.NEW)
            start();
        journal.publishError(this);

        return this;
    }


    /**
     * Records the timestamp where this event occurred.
     *
     * No need to handle this manually, unless you don't want to broadcast the start event.
     *
     * @return Event    this event
     */
    @Override
    public Event start()
    {
        state               = EventState.STARTED;
        eventStartedAtMS    = System.currentTimeMillis();

        return this;
    }

    /**
     * Records the timestamp where this event occurred AND returns the parent of this event.
     *
     * @return Event the parent of this event
     */
    @Override
    public Event stop()
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


    /**
     * Getter of attributes
     *
     * @return the list of attributes
     */
    @Override
    public Map<String,Attribute> attributes()
    {
        return attributes;
    }

    /**
     * @return duration of the event in milliseconds, or 0 if non-started/non-stopped.
     */
    @Override
    public long durationInMS()
    {
        if (eventStartedAtMS > 0 && eventEndedAtMS > 0)
            return (eventEndedAtMS - eventStartedAtMS);
        else
            return 0;
    }

    @Override
    public Event parent() {
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
    public Event ponctualEvent()
    {
        this.isPonctual     = true;
        this.state          = EventState.ENDED;
        eventStartedAtMS    = System.currentTimeMillis();
        eventEndedAtMS      = eventStartedAtMS;

        return this;
    }

    @Override
    public boolean isPonctual() {
        return this.isPonctual;
    }

    protected EventJournal journal() {
        return this.journal;
    }

}
