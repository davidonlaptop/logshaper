package net.davidlauzon.slimevents;


import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by david on 15-11-04.
 *
 * Log levels hierarchy: FATAL > ERROR > WARN > INFO > DEBUG > TRACE
 */
public class SlimEvent
{
    private EventsRegistry registry;
    private SlimEvent parent;

    private String eventName;
    private int depth;
    private long eventStartedAtMS;
    private long eventEndedAtMS;
    private SlimEventState state;

    private Map<String,Attribute> attributes;


    /**
     * Package-private constructor (reserved for internal use). See @EventsRegistry for how to initialize the root event
     *
     * @param parent    The parent that triggered this new event.
     * @param depth     The level of depth from the root event (0 if no parent)
     * @param name      The name of the Event
     */
    SlimEvent( EventsRegistry registry, String name, int depth, SlimEvent parent )
    {
        this.registry   = registry;
        this.eventName  = name;
        this.depth      = depth;
        this.parent     = parent;
        this.attributes = new LinkedHashMap<>();

        eventStartedAtMS = System.currentTimeMillis();
        state            = SlimEventState.NEW;
    }


    /**
     * Starts a new child event of the current event
     *
     * @param name The name of the event
     * @return the event newly created
     */
    public SlimEvent createChild( String name )
    {
        return new SlimEvent( registry, name, depth + 1, this );
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
     * @return SlimEvent    The current counter.
     */
    public SlimEvent count(String name, long value)
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
     * @return SlimEvent    this event
     */
    public SlimEvent attr( String name, String value )
    {
        this.attributes.put( name, new TextAttribute(value) );

        return this;
    }


    public SlimEvent trace()
    {
        if (state == SlimEventState.NEW)
            start();
        registry.publishTrace(this);

        return this;
    }

    public SlimEvent debug()
    {
        if (state == SlimEventState.NEW)
            start();
        registry.publishDebug(this);

        return this;
    }

    public SlimEvent info()
    {
        if (state == SlimEventState.NEW)
            start();
        registry.publishInfo(this);

        return this;
    }

    public SlimEvent warn()
    {
        if (state == SlimEventState.NEW)
            start();
        registry.publishWarn(this);

        return this;
    }

    public SlimEvent error()
    {
        if (state == SlimEventState.NEW)
            start();
        registry.publishError(this);

        return this;
    }


    /**
     * Records the timestamp where this event occurred.
     *
     * No need to handle this manually, unless you don't want to broadcast the start event.
     *
     * @return SlimEvent    this event
     */
    public SlimEvent start()
    {
        state               = SlimEventState.STARTED;
        eventStartedAtMS    = System.currentTimeMillis();

        return this;
    }

    /**
     * Records the timestamp where this event occurred AND returns the parent of this event.
     *
     * @return SlimEvent the parent of this event
     */
    public SlimEvent stop()
    {
        eventEndedAtMS  = System.currentTimeMillis();
        state           = SlimEventState.ENDED;

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
     */
    public Map<String,Attribute> attributes()
    {
        return attributes;
    }

    /**
     * @return duration of the event in milliseconds, or 0 if non-started/non-stopped.
     */
    public long durationInMS()
    {
        if (eventStartedAtMS > 0 && eventEndedAtMS > 0)
            return (eventEndedAtMS - eventStartedAtMS);
        else
            return 0;
    }

    public SlimEvent parent() {
        return parent;
    }

    public String eventName()
    {
        return eventName;
    }

    public SlimEventState state()
    {
        return state;
    }

    public int depth() {
        return depth;
    }


    static public abstract class Attribute
    {
        protected Type type;

        public Type type() {
            return type;
        }

        public enum Type
        {
            Text,
            Counter,
            DurationCounter
        }

        public abstract String stringValue();
    }

    static public class TextAttribute extends Attribute
    {
        protected String value;

        public TextAttribute( String value )
        {
            this.value = value;
            this.type  = Type.Text;
        }

        public String value() {
            return value;
        }

        @Override
        public String stringValue() {
            return String.valueOf( value );
        }
    }

    static public class CounterAttribute extends Attribute
    {
        protected long value;

        public CounterAttribute( long value )
        {
            this.value = value;
            this.type  = Type.Counter;
        }

        public void increment(long increment) {
            this.value += increment;
        }

        public long value() {
            return value;
        }

        @Override
        public String stringValue() {
            return String.valueOf( value );
        }
    }

    static public class DurationAttribute extends CounterAttribute
    {
        public DurationAttribute( long value )
        {
            super( value );
            this.type  = Type.DurationCounter;
        }

        public float seconds() {
            return value / 1000f;
        }
    }
}
