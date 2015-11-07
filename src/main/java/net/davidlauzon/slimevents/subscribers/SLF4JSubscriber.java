package net.davidlauzon.slimevents.subscribers;

import net.davidlauzon.slimevents.Event;
import net.davidlauzon.slimevents.Subscriber;
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
        log.trace( formatEvent(event) );
    }

    @Override
    public void onDebug(Event event) {
        log.debug( formatEvent(event) );
    }

    @Override
    public void onInfo(Event event) {
        log.info( formatEvent(event) );
    }

    @Override
    public void onWarn(Event event) {
        log.warn( formatEvent(event) );
    }

    @Override
    public void onError(Event event) {
        log.error( formatEvent(event) );
    }
}
