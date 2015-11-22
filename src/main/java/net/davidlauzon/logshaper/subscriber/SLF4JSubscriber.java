package net.davidlauzon.logshaper.subscriber;

import net.davidlauzon.logshaper.event.Event;
import org.slf4j.Logger;

/**
 * Created by david on 15-11-05.
 */
public class SLF4JSubscriber extends AbstractSubscriber implements Subscriber
{
    public Logger log;


    public SLF4JSubscriber(Logger log)
    {
        this.log = log;
    }

    @Override
    public void onTrace(Event event) {
        if (log.isTraceEnabled())
            log.trace( formatEvent(event) );
    }

    @Override
    public void onDebug(Event event) {
        if (log.isDebugEnabled())
            log.debug( formatEvent(event) );
    }

    @Override
    public void onInfo(Event event) {
        if (log.isInfoEnabled())
            log.info( formatEvent(event) );
    }

    @Override
    public void onWarn(Event event) {
        if (log.isWarnEnabled())
            log.warn( formatEvent(event) );
    }

    @Override
    public void onError(Event event) {
        if (log.isErrorEnabled())
            log.error( formatEvent(event) );
    }
}
