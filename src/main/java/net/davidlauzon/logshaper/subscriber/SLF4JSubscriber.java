package net.davidlauzon.logshaper.subscriber;

import net.davidlauzon.logshaper.event.LogEvent;
import org.slf4j.Logger;

/**
 * Created by david on 15-11-05.
 */
public class SLF4JSubscriber extends AbstractSubscriber
{
    public Logger log;


    public SLF4JSubscriber(Logger log)
    {
        this.log = log;
    }

    @Override
    public void onTrace(LogEvent event)
    {
        if (log.isTraceEnabled())
            log.trace( formatEvent(event) );
    }

    @Override
    public void onTrace(LogEvent event, Throwable throwable)
    {
        if (log.isTraceEnabled())
            log.trace( formatEvent(event), throwable );
    }

    @Override
    public void onDebug(LogEvent event)
    {
        if (log.isDebugEnabled())
            log.debug( formatEvent(event) );
    }

    @Override
    public void onDebug(LogEvent event, Throwable throwable)
    {
        if (log.isDebugEnabled())
            log.debug( formatEvent(event), throwable );
    }

    @Override
    public void onInfo(LogEvent event)
    {
        if (log.isInfoEnabled())
            log.info(formatEvent(event));
    }

    @Override
    public void onInfo(LogEvent event, Throwable throwable)
    {
        if (log.isInfoEnabled())
            log.info( formatEvent(event), throwable );
    }

    @Override
    public void onWarn(LogEvent event)
    {
        if (log.isWarnEnabled())
            log.warn( formatEvent(event) );
    }

    @Override
    public void onWarn(LogEvent event, Throwable throwable)
    {
        if (log.isWarnEnabled())
            log.warn( formatEvent(event), throwable );
    }

    @Override
    public void onError(LogEvent event)
    {
        if (log.isErrorEnabled())
            log.error( formatEvent(event) );
    }

    @Override
    public void onError(LogEvent event, Throwable throwable)
    {
        if (log.isErrorEnabled())
            log.error( formatEvent(event), throwable );
    }
}
