package net.davidlauzon.logshaper;


import net.davidlauzon.logshaper.event.DefaultEvent;
import net.davidlauzon.logshaper.event.Event;
import net.davidlauzon.logshaper.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 15-11-04.
 */
public class EventJournal
{
    private List<Subscriber> subscribers;


    public EventJournal()
    {
        subscribers = new ArrayList<>();
    }


    public void subscribe( Subscriber subscriber )
    {
        subscribers.add( subscriber );
    }


    public Event createRootEvent(String name)
    {
        return new DefaultEvent( this, name, 0, null );
    }


    /**
     * Reserved for internal use
      * @param event
     */
    public void publishTrace( Event event )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onTrace( event );
    }

    public void publishTrace( Event event, Throwable throwable )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onTrace( event, throwable );
    }

    public void publishDebug( Event event )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onDebug( event );
    }

    public void publishDebug( Event event, Throwable throwable )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onDebug( event, throwable );
    }

    public void publishInfo( Event event )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onInfo( event );
    }

    public void publishInfo( Event event, Throwable throwable )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onInfo( event, throwable );
    }

    public void publishWarn( Event event )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onWarn( event );
    }

    public void publishWarn( Event event, Throwable throwable )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onWarn( event, throwable );
    }

    public void publishError( Event event )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onError( event );
    }

    public void publishError( Event event, Throwable throwable )
    {
        for (Subscriber subscriber : subscribers)
            subscriber.onError( event, throwable );
    }
}
