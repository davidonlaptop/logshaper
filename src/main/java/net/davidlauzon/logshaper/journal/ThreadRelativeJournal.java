package net.davidlauzon.logshaper.journal;

import net.davidlauzon.logshaper.event.ClearStackEvent;
import net.davidlauzon.logshaper.event.DefaultEvent;
import net.davidlauzon.logshaper.event.LogEvent;

import java.util.*;

/**
 * Created by david on 15-11-27.
 *
 * A journal that maintains a stack relative to a thread.
 *
 * Use this implementation if you don't want to be required to pass the event as a Data Transfer Object through all the
 * layers of your architecture in order to create child event.
 *
 * Instead, you can just use the journal's newRelativeChildEvent(), newPonctualEvent(), or newThrowableEvent() methods,
 * which will return a child relative to the latest event in stack of the current thread. There is one stack per thread.
 * The journal adds the events to the stack as they are started, and removes them automatically once they are stopped.
 *
 * However, it is *NOT* fully thread-safe. Do NOT use this implementation if you need to follow a chain of events
 * across multiple threads.
 */
public class ThreadRelativeJournal extends SimpleJournal
{
    /**
     * This map maintains a stack of LogEvent keyed by Thread ID.
     * The map does not need to be concurrent because each Thread is using a different key.
     * The value is Deque (stack or double-edge queue) of LogEvent.
     */
    private Map<Long,Deque<LogEvent>> stacksByThread = new HashMap<>();


    @Override
    public LogEvent newRootEvent(String name)
    {
        ClearStackEvent clearStackEvent = clearStack();

        // Only publish the ClearStack event if the stack was non-empty
        if (clearStackEvent.nbEventsCleared() > 0) {
            // If you don't like this automatic warning, then clear the stack manually by yourself
            clearStackEvent.attr(ClearStackEvent.AUTOMATIC_ATTR, true)
                    .publishWarn();
        }

        return new DefaultEvent( this, name, null, true );
    }

    @Override
    public LogEvent newRelativeChildEvent(String name)
    {
        return relativeCurrentEvent().newChildEvent(name);
    }

    @Override
    public LogEvent newPonctualEvent(String name)
    {
        return relativeCurrentEvent().newPonctualEvent(name);
    }

    @Override
    public LogEvent newThrowableEvent(Throwable throwable)
    {
        return relativeCurrentEvent().newThrowableEvent(throwable);
    }

    @Override
    public EventJournal onEventStarted(LogEvent event)
    {
        if (!event.isPonctual())
            stackPushEvent(event);

        return this;
    }

    @Override
    public EventJournal onEventStopped(LogEvent event)
    {
        if (!event.isPonctual())
            stackRemoveEvent(event);

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
        LogEvent currentEvent = relativeCurrentEvent();
        Deque<LogEvent> stack = getOrCreateRelativeStack();

        int depth = stack.size();
        if (depth > 0) {
            currentEvent.stopAll();
            stack.clear();              // Ensure stack is empty
        }

        // Creating a special event to keep track of the current stack that will be cleared.
        // PonctualEvent are NOT added to the stack
        ClearStackEvent clearStackEvent = new ClearStackEvent( this, ClearStackEvent.DEFAULT_NAME, currentEvent, true );
        clearStackEvent.setNbEventsCleared( depth );

        return clearStackEvent;
    }

    protected LogEvent relativeCurrentEvent()
    {
        Deque<LogEvent> stack = getOrCreateRelativeStack();

        return stack.peekLast();
    }

    protected void stackPushEvent( LogEvent event )
    {
        getOrCreateRelativeStack().addLast( event );
    }

    protected LogEvent stackPopEvent()
    {
        return getOrCreateRelativeStack().removeLast();
    }

    /**
     * Remove a specific event from the stack
     *
     * @param eventToRemove
     * @return
     */
    protected EventJournal stackRemoveEvent( LogEvent eventToRemove )
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
}
