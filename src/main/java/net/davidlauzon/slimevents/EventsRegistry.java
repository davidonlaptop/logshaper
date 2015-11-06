package net.davidlauzon.slimevents;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 15-11-04.
 */
public class EventsRegistry
{
    private List<SlimEventsSubscriber> subscribers;


    public EventsRegistry()
    {
        subscribers = new ArrayList<>();
    }


    public void subscribe( SlimEventsSubscriber subscriber )
    {
        subscribers.add( subscriber );
    }


    public SlimEvent createRootEvent(String name)
    {
        return new SlimEvent( this, name, 0, null );
    }


    /**
     * package-private (reserved for internal use)
      * @param event
     */
    void publishTrace( SlimEvent event )
    {
        for (SlimEventsSubscriber subscriber : subscribers) {
            subscriber.onTrace( event );
        }
    }

    void publishDebug( SlimEvent event )
    {
        for (SlimEventsSubscriber subscriber : subscribers) {
            subscriber.onDebug(event);
        }
    }

    void publishInfo( SlimEvent event )
    {
        for (SlimEventsSubscriber subscriber : subscribers) {
            subscriber.onInfo(event);
        }
    }

    void publishWarn( SlimEvent event )
    {
        for (SlimEventsSubscriber subscriber : subscribers) {
            subscriber.onWarn(event);
        }
    }

    void publishError( SlimEvent event )
    {
        for (SlimEventsSubscriber subscriber : subscribers) {
            subscriber.onError(event);
        }
    }
}
