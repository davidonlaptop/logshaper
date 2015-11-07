package net.davidlauzon.slimevents;

/**
 * Created by david on 15-11-06.
 */
public class SlimEvent
{
    static private final EventRegistry defaultRegistry = new EventRegistry();


    static public final EventRegistry getDefaultRegistry() {
        return defaultRegistry;
    }


    /**
     * Create a new root event in the default registry
     * @param name The name of the event
     * @return the event
     */
    static public Event createRootEvent( String name )
    {
        return getDefaultRegistry().createRootEvent( name );
    }
}
