package net.davidlauzon.logshaper.subscriber;

import net.davidlauzon.logshaper.event.LogEvent;

/**
 * Created by david on 15-11-05.
 */
public interface LogSubscriber
{
    void onTrace( LogEvent event );
    void onTrace( LogEvent event, Throwable throwable );
    void onDebug( LogEvent event );
    void onDebug( LogEvent event, Throwable throwable );
    void onInfo( LogEvent event );
    void onInfo( LogEvent event, Throwable throwable );
    void onWarn( LogEvent event );
    void onWarn( LogEvent event, Throwable throwable );
    void onError( LogEvent event );
    void onError( LogEvent event, Throwable throwable );
}
