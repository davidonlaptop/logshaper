package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.journal.EventJournal;
import net.davidlauzon.logshaper.attribute.Attribute;
import net.davidlauzon.logshaper.attribute.LongAttribute;

/**
 * Created by david on 15-11-27.
 *
 * Event used by the relative journal
 */
public class ClearStackEvent extends PonctualEvent
{
    static public final String DEFAULT_NAME   = "ClearStack";
    static public final String NB_EVENTS_ATTR = "NbEventsCleared";
    static public final String AUTOMATIC_ATTR = "Automatic";


    public ClearStackEvent(EventJournal journal, String name, LogEvent parent, boolean isRelative)
    {
        super(journal, name, parent, isRelative);
    }


    /**
     * Sets the number of cleared events
     *
     * @param nbEventsCleared
     * @return this event
     */
    public ClearStackEvent setNbEventsCleared(long nbEventsCleared)
    {
        count(NB_EVENTS_ATTR, nbEventsCleared);

        return this;
    }

    /**
     * Returns the number of cleared events
     */
    public long nbEventsCleared()
    {
        Attribute attr = attributes().get(NB_EVENTS_ATTR);
        if (attr != null) {
            return ((LongAttribute) attr).value();
        } else {
            return 0;
        }
    }

    @Override
    public LogEvent publishTrace()
    {
        super.publishTrace();

        // Also publish recursively the events cleared from the stack
        LogEvent previousEvent = parent();
        while (previousEvent != null)
        {
            previousEvent.publishTrace();
            previousEvent = previousEvent.parent();
        }

        return this;
    }

    @Override
    public LogEvent publishDebug()
    {
        super.publishDebug();

        // Also publish recursively the events cleared from the stack
        LogEvent previousEvent = parent();
        while (previousEvent != null)
        {
            previousEvent.publishDebug();
            previousEvent = previousEvent.parent();
        }

        return this;
    }

    @Override
    public LogEvent publishInfo()
    {
        super.publishInfo();

        // Also publish recursively the events cleared from the stack
        LogEvent previousEvent = parent();
        while (previousEvent != null)
        {
            previousEvent.publishInfo();
            previousEvent = previousEvent.parent();
        }

        return this;
    }

    @Override
    public LogEvent publishWarn()
    {
        super.publishWarn();

        // Also publish recursively the events cleared from the stack
        LogEvent previousEvent = parent();
        while (previousEvent != null)
        {
            previousEvent.publishWarn();
            previousEvent = previousEvent.parent();
        }

        return this;
    }

    @Override
    public LogEvent publishError()
    {
        super.publishError();

        // Also publish recursively the events cleared from the stack
        LogEvent previousEvent = parent();
        while (previousEvent != null)
        {
            previousEvent.publishError();
            previousEvent = previousEvent.parent();
        }

        return this;
    }
}
