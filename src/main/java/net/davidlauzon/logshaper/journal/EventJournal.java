package net.davidlauzon.logshaper.journal;

import net.davidlauzon.logshaper.event.ClearStackEvent;
import net.davidlauzon.logshaper.event.LogEvent;
import net.davidlauzon.logshaper.subscriber.LogSubscriber;

/**
 * Created by david on 15-11-27.
 */
public interface EventJournal
{
    /**
     * Add a subscriber to the journal
     *
     * @param subscriber
     */
    public EventJournal subscribe(LogSubscriber subscriber);

    /**
     * Adds an event without a parent.
     *
     * @param name
     * @return the event
     */
    public LogEvent newRootEvent(String name);

    /**
     * Adds a child event relative to the current event.
     *
     * @param name
     * @return the child event
     */
    public LogEvent newRelativeChildEvent(String name);

    /**
     * Adds an event with no start/end (duration of 0 seconds).
     *
     * @param name
     * @return the event
     */
    public LogEvent newPonctualEvent(String name);

    /**
     * Adds an Exception or Error.
     * @param throwable
     * @return the event
     */
    public LogEvent newThrowableEvent(Throwable throwable);

    /**
     * Callback hook triggered when a journal event is started.
     *
     * @param event
     * @return
     */
    public EventJournal onEventStarted(LogEvent event);

    /**
     * Callback hook triggered when a journal event is stopped.
     * @param event
     * @return
     */
    public EventJournal onEventStopped(LogEvent event);

    /**
     * Callback hook triggered when a journal ponctual event is created.
     * @param event
     * @return
     */
    public EventJournal onPonctualEvent(LogEvent event);

    /**
     * Clears the stack of event.
     *
     * Assess @ClearStackEvent.nbEventsCleared() to check how many events were actually cleared.
     *
     * @return a new event containing all the cleared stack events
     */
    public ClearStackEvent clearStack();

    /**
     * Publish the event to the subscribers.
     * @param event
     * @return this journal
     */
    public EventJournal publishTrace(LogEvent event);
    public EventJournal publishTrace(LogEvent event, Throwable throwable);

    public EventJournal publishDebug(LogEvent event);
    public EventJournal publishDebug(LogEvent event, Throwable throwable);

    public EventJournal publishInfo(LogEvent event);
    public EventJournal publishInfo(LogEvent event, Throwable throwable);

    public EventJournal publishWarn(LogEvent event);
    public EventJournal publishWarn(LogEvent event, Throwable throwable);

    public EventJournal publishError(LogEvent event);
    public EventJournal publishError(LogEvent event, Throwable throwable);
}
