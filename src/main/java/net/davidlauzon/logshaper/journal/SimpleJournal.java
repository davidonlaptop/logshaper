package net.davidlauzon.logshaper.journal;


import net.davidlauzon.logshaper.event.*;
import net.davidlauzon.logshaper.subscriber.LogSubscriber;

import java.util.*;


/**
 * Created by david on 15-11-04.
 */
public class SimpleJournal implements EventJournal
{
    protected List<LogSubscriber> subscribers;


    public SimpleJournal()
    {
        subscribers = new ArrayList<>();
    }


    @Override
    public EventJournal subscribe(LogSubscriber subscriber)
    {
        subscribers.add( subscriber );

        return this;
    }


    @Override
    public LogEvent newRootEvent(String name)
    {
        boolean isRelative = false;

        return new DefaultEvent( this, name, null, isRelative );
    }


    @Override
    public LogEvent newRelativeChildEvent(String name)
    {
        LogEvent event = newRootEvent(name);

        event.newPonctualEvent( LogEvent.UNSUPPORTED_LOGSHAPER_OPERATION_EVENT )
                .attr("class", getClass().getName())
                .attr("method", "newRelativeChildEvent")
                .publishWarn();

        return event;
    }

    @Override
    public LogEvent newPonctualEvent(String name)
    {
        boolean isRelative = false;

        return new PonctualEvent( this, name, null, isRelative );
    }

    @Override
    public LogEvent newThrowableEvent(Throwable throwable)
    {
        boolean isRelative = false;

        return new ThrowableEvent( this, throwable, null, isRelative );
    }

    @Override
    public EventJournal onEventStarted(LogEvent event)
    {
        return this;
    }

    @Override
    public EventJournal onEventStopped(LogEvent event)
    {
        return this;
    }

    @Override
    public EventJournal onPonctualEvent(LogEvent event)
    {
        return this;
    }

    @Override
    public ClearStackEvent clearStack()
    {
        // Fake this method as we still need to implement the interface's method even if the journal has no stack.
        boolean isRelative = false;
        ClearStackEvent clearStackEvent = new ClearStackEvent( this, ClearStackEvent.DEFAULT_NAME, null, isRelative );
        clearStackEvent.setNbEventsCleared( 0 );

        return clearStackEvent;
    }


    @Override
    public EventJournal publishTrace(LogEvent event)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onTrace( event );

        return this;
    }

    @Override
    public EventJournal publishTrace(LogEvent event, Throwable throwable)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onTrace( event, throwable );

        return this;
    }

    @Override
    public EventJournal publishDebug(LogEvent event)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onDebug( event );

        return this;
    }

    @Override
    public EventJournal publishDebug(LogEvent event, Throwable throwable)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onDebug( event, throwable );

        return this;
    }

    @Override
    public EventJournal publishInfo(LogEvent event)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onInfo( event );

        return this;
    }

    @Override
    public EventJournal publishInfo(LogEvent event, Throwable throwable)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onInfo( event, throwable );

        return this;
    }

    @Override
    public EventJournal publishWarn(LogEvent event)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onWarn( event );

        return this;
    }

    @Override
    public EventJournal publishWarn(LogEvent event, Throwable throwable)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onWarn( event, throwable );

        return this;
    }

    @Override
    public EventJournal publishError(LogEvent event)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onError( event );

        return this;
    }

    @Override
    public EventJournal publishError(LogEvent event, Throwable throwable)
    {
        for (LogSubscriber subscriber : subscribers)
            subscriber.onError( event, throwable );

        return this;
    }
}
