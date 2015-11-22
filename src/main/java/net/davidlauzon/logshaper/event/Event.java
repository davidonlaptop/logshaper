package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.attribute.Attribute;

import java.util.Map;

/**
 * Created by david on 15-11-07.
 */
public interface Event
{
    /**
     * Starts a new child event of the current event
     *
     * @param name The name of the event
     * @return the event newly created
     */
    public Event createChild(String name);

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
    public Event count(String name, long value);

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
    public Event attr(String name, String value);

    public Event publishTrace();

    public Event publishDebug();

    public Event publishInfo();

    public Event publishWarn();

    public Event publishError();

    /**
     * Records the timestamp where this event occurred.
     *
     * No need to handle this manually, unless you don't want to broadcast the start event.
     *
     * @return Event    this event
     */
    public Event start();

    /**
     * Records the timestamp where this event occurred AND returns the parent of this event.
     *
     * @return Event the parent of this event
     */
    public Event stop();

    /**
     * Getter of attributes
     *
     * @return the list of attributes
     */
    public Map<String,Attribute> attributes();

    /**
     * @return duration of the event in milliseconds, or 0 if non-started/non-stopped.
     */
    public long durationInMS();

    public Event parent();

    public String eventName();

    public EventState state();

    public int depth();
}
