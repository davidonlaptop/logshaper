package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.attribute.Attribute;

import java.util.Map;

/**
 * Created by david on 15-11-07.
 */
public interface LogEvent
{
    /**
     * Starts a new child event of the current event
     *
     * @param name The name of the event
     * @return the event newly created
     */
    public LogEvent createChild(String name);

    /**
     * Starts a new "Throwable" child event of the current event
     *
     * @param name The name of the event
     * @param throwable the Throwable Exception or Error
     * @return the event newly created
     */
    public LogEvent createChild(String name, Throwable throwable);

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
     * @return LogEvent    The current counter.
     */
    public LogEvent count(String name, long value);

    /**
     * Sets an attribute of this event.
     *
     * The scope is always local to the event (e.g. is it NOT propagated to the parents like the counters are).
     *
     * Exemples: User, SessionId, IP, UserAction, URL, Method, UserAgent
     *
     * @param name          The attribute name
     * @param value         The attribute value
     * @return LogEvent    this event
     */
    public LogEvent attr(String name, String value);

    public LogEvent publishTrace();

    public LogEvent publishDebug();

    public LogEvent publishInfo();

    public LogEvent publishWarn();

    public LogEvent publishError();

    /**
     * Records the timestamp where this event occurred.
     *
     * No need to handle this manually, unless you don't want to broadcast the start event.
     *
     * @return LogEvent    this event
     */
    public LogEvent start();

    /**
     * Records the timestamp where this event occurred AND returns the parent of this event.
     *
     * @return LogEvent the parent of this event
     */
    public LogEvent stop();

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

    public LogEvent parent();

    public String eventName();

    public EventState state();

    public int depth();

    /**
     * Indicates that this event is a ponctualEvent event.
     *
     * Ponctual: An action considered as having no temporal duration. http://www.thefreedictionary.com/punctual
     *
     * @return LogEvent
     */
    public LogEvent ponctualEvent();
    public boolean isPonctual();
}
