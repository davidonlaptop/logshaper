package net.davidlauzon.logshaper;


import net.davidlauzon.logshaper.event.DefaultEvent;
import net.davidlauzon.logshaper.event.LogEvent;
import net.davidlauzon.logshaper.subscriber.LogSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 15-11-04.
 */
public class EventJournal
{
    private List<LogSubscriber> subscribers;


    public EventJournal()
    {
        subscribers = new ArrayList<>();
    }


    public void subscribe( LogSubscriber subscriber )
    {
        subscribers.add( subscriber );
    }


    public LogEvent createRootEvent(String name)
    {
        return new DefaultEvent( this, name, 0, null );
    }


    /**
     * Reserved for internal use
      * @param event
     */
    public void publishTrace( LogEvent event )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onTrace( event );
    }

    public void publishTrace( LogEvent event, Throwable throwable )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onTrace( event, throwable );
    }

    public void publishDebug( LogEvent event )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onDebug( event );
    }

    public void publishDebug( LogEvent event, Throwable throwable )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onDebug( event, throwable );
    }

    public void publishInfo( LogEvent event )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onInfo( event );
    }

    public void publishInfo( LogEvent event, Throwable throwable )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onInfo( event, throwable );
    }

    public void publishWarn( LogEvent event )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onWarn( event );
    }

    public void publishWarn( LogEvent event, Throwable throwable )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onWarn( event, throwable );
    }

    public void publishError( LogEvent event )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onError( event );
    }

    public void publishError( LogEvent event, Throwable throwable )
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onError( event, throwable );
    }
}
