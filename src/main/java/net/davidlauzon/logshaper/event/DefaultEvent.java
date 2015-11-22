package net.davidlauzon.logshaper.event;


import net.davidlauzon.logshaper.EventRegistry;
import net.davidlauzon.logshaper.event.attribute.Attribute;
import net.davidlauzon.logshaper.event.attribute.CounterAttribute;
import net.davidlauzon.logshaper.event.attribute.TextAttribute;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by david on 15-11-04.
 *
 * Log levels hierarchy: FATAL, ERROR, WARN, INFO, DEBUG, TRACE
 */
public class DefaultEvent implements Event
{
    private EventRegistry registry;
    private Event parent;

    private String eventName;
    private int depth;
    private long eventStartedAtMS;
    private long eventEndedAtMS;
    private EventState state;

    private Map<String,Attribute> attributes;


    /**
     * (Constructor reserved for internal use). See @EventRegistry for how to initialize the root event
     *
     * @param parent    The parent that triggered this new event.
     * @param depth     The level of depth from the root event (0 if no parent)
     * @param name      The name of the Event
     */
    public DefaultEvent(EventRegistry registry, String name, int depth, Event parent)
    {
        this.registry   = registry;
        this.eventName  = name;
        this.depth      = depth;
        this.parent     = parent;
        this.attributes = new LinkedHashMap<>();

        eventStartedAtMS = System.currentTimeMillis();
        state            = EventState.NEW;
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
        return new DefaultEvent( registry, name, depth + 1, this );
    }


    /**
     * Creates or updates a counter with the given name and adds it the specified value.
     *
     * The Counter is automatically propagated recursively to all the parents.
     *
     * To decrement the counter, just send a negative value.
     *
     * Exemples: "Duration.DB", "Duration.Alfresco", "Duration.Birt", "Duration.JsonParsing", "ComputingBudget"
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
    public Event trace()
    {
        if (state == EventState.NEW)
            start();
        registry.publishTrace(this);

        return this;
    }

    @Override
    public Event debug()
    {
        if (state == EventState.NEW)
            start();
        registry.publishDebug(this);

        return this;
    }

    @Override
    public Event info()
    {
        if (state == EventState.NEW)
            start();
        registry.publishInfo(this);

        return this;
    }

    @Override
    public Event warn()
    {
        if (state == EventState.NEW)
            start();
        registry.publishWarn(this);

        return this;
    }

    @Override
    public Event error()
    {
        if (state == EventState.NEW)
            start();
        registry.publishError(this);

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
        return this.eventName.replace(' ', '.') + ".Duration";
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


}
