package net.davidlauzon.slimevents.subscribers;

import net.davidlauzon.slimevents.SlimEvent;
import net.davidlauzon.slimevents.SlimEventsSubscriber;
import org.slf4j.Logger;

/**
 * Created by david on 15-11-05.
 */
public class SLF4JSubscriber extends AbstractSubscriber implements SlimEventsSubscriber
{
    public Logger log;


    public SLF4JSubscriber(Logger log)
    {
        this.log = log;
    }

    @Override
    public void onTrace(SlimEvent event) {
        log.trace( formatEvent(event) );
    }

    @Override
    public void onDebug(SlimEvent event) {
        log.debug( formatEvent(event) );
    }

    @Override
    public void onInfo(SlimEvent event) {
        log.info( formatEvent(event) );
    }

    @Override
    public void onWarn(SlimEvent event) {
        log.warn( formatEvent(event) );
    }

    @Override
    public void onError(SlimEvent event) {
        log.error( formatEvent(event) );
    }
}
