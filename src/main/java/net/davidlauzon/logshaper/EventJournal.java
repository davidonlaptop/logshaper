package net.davidlauzon.logshaper;


import net.davidlauzon.logshaper.event.ClearStackEvent;
import net.davidlauzon.logshaper.event.DefaultEvent;
import net.davidlauzon.logshaper.event.EventState;
import net.davidlauzon.logshaper.event.LogEvent;
import net.davidlauzon.logshaper.subscriber.LogSubscriber;

import java.util.*;


/**
 * Created by david on 15-11-04.
 */
public class EventJournal
{
    static public final String CLEAR_STACK_EVENT_NAME = "ClearStack";

    static public final String CLEAR_STACK_AUTOMATIC = "Automatic";


    private List<LogSubscriber> subscribers;

    /**
     * This map maintains a stack of LogEvent keyed by Thread ID.
     * The map does not need to be concurrent because each Thread is using a different key.
     * The value is Deque (stack or double-edge queue) of LogEvent.
     */
    private Map<Long,Deque<LogEvent>> stacksByThread = new HashMap<>();


    public EventJournal()
    {
        subscribers = new ArrayList<>();
    }


    public void subscribe( LogSubscriber subscriber )
    {
        subscribers.add( subscriber );
    }


    public LogEvent newRootEvent(String name)
    {
        return new DefaultEvent( this, name, 0, null );
    }


    public LogEvent newRelativeRootEvent(String name)
    {
        ClearStackEvent clearStackEvent = clearRelativeStack();
        clearStackEvent.attributes().get(CLEAR_STACK_NB_EVENTS).;

        // Only publish the ClearStack event if the stack was non-empty
        if (clearStackEvent.nbEventsCleared() > 0) {
            // If you don't like this automatic warning, then clear the stack manually by yourself
            clearStackEvent.attr(CLEAR_STACK_AUTOMATIC, true)
                    .publishWarn();
        }

        return new DefaultEvent( this, name, 0, null, true );
    }

    public LogEvent newRelativeChildEvent(String name)
    {
        return relativeCurrentEvent().newChildEvent(name);
    }

    public LogEvent newRelativePonctualEvent(String name)
    {
        return relativeCurrentEvent().newPonctualEvent(name);
    }

    public LogEvent newRelativeThrowableEvent(Throwable throwable)
    {
        return relativeCurrentEvent().newThrowableEvent(throwable);
    }

    public ClearStackEvent clearRelativeStack()
    {
        LogEvent currentEvent = relativeCurrentEvent();
        Deque<LogEvent> stack = getOrCreateRelativeStack();

        int depth = stack.size();
        if (depth > 0) {
            currentEvent.stopAll();
            stack.clear();              // Ensure stack is empty
        }

        // Creating a special event to keep track of the current stack that will be cleared.
        // PonctualEvent are NOT added to the stack
        ClearStackEvent clearStackEvent = new ClearStackEvent( this, CLEAR_STACK_EVENT_NAME, depth, currentEvent, true );
        clearStackEvent.count( CLEAR_STACK_NB_EVENTS, depth );

        return clearStackEvent;
    }

    public LogEvent relativeCurrentEvent()
    {
        Deque<LogEvent> stack = getOrCreateRelativeStack();

        return stack.getLast();
    }

    public void stackPushEvent( LogEvent event )
    {
        getOrCreateRelativeStack().addLast( event );
    }

    public LogEvent stackPopEvent()
    {
        return getOrCreateRelativeStack().removeLast();
    }

    /**
     * Remove a specific event from the stack
     *
     * @param eventToRemove
     * @return
     */
    public EventJournal stackRemoveEvent( LogEvent eventToRemove )
    {
        boolean success = getOrCreateRelativeStack().removeLastOccurrence(eventToRemove);

        return this;
    }

    private Deque<LogEvent> getOrCreateRelativeStack()
    {
        long threadId         = Thread.currentThread().getId();
        Deque<LogEvent> stack = this.stacksByThread.get(threadId);

        if (stack == null) {
            stack = new ArrayDeque<>();

            this.stacksByThread.put( threadId, stack );
        }

        return stack;
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
