package net.davidlauzon.slimevents;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 15-11-04.
 */
public class EventRegistry
{
    private List<Subscriber> subscribers;


    public EventRegistry()
    {
        subscribers = new ArrayList<>();
    }


    public void subscribe( Subscriber subscriber )
    {
        subscribers.add( subscriber );
    }


    public Event createRootEvent(String name)
    {
        return new Event( this, name, 0, null );
    }


    /**
     * package-private (reserved for internal use)
      * @param event
     */
    void publishTrace( Event event )
    {
        for (Subscriber subscriber : subscribers) {
            subscriber.onTrace( event );
        }
    }

    void publishDebug( Event event )
    {
        for (Subscriber subscriber : subscribers) {
            subscriber.onDebug(event);
        }
    }

    void publishInfo( Event event )
    {
        for (Subscriber subscriber : subscribers) {
            subscriber.onInfo(event);
        }
    }

    void publishWarn( Event event )
    {
        for (Subscriber subscriber : subscribers) {
            subscriber.onWarn(event);
        }
    }

    void publishError( Event event )
    {
        for (Subscriber subscriber : subscribers) {
            subscriber.onError(event);
        }
    }
}
