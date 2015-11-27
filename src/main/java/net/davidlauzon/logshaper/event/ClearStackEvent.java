package net.davidlauzon.logshaper.event;

import net.davidlauzon.logshaper.EventJournal;
import net.davidlauzon.logshaper.attribute.Attribute;
import net.davidlauzon.logshaper.attribute.LongAttribute;

/**
 * Created by david on 15-11-27.
 *
 * Event used by the relative journal
 */
public class ClearStackEvent extends PonctualEvent
{
    static protected final String CLEAR_STACK_NB_EVENTS = "NbEventsCleared";


    public ClearStackEvent(EventJournal journal, String name, int depth, LogEvent parent, boolean isRelative)
    {
        super(journal, name, depth, parent, isRelative);
    }


    public ClearStackEvent nbEventsCleared( long nbEventsCleared )
    {
        count(CLEAR_STACK_NB_EVENTS, nbEventsCleared);

        return this;
    }

    public long nbEventsCleared()
    {
        Attribute attr = attributes().get(CLEAR_STACK_NB_EVENTS);
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
