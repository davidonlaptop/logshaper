package net.davidlauzon.logshaper.subscriber;

import net.davidlauzon.logshaper.event.Event;

/**
 * Created by david on 15-11-05.
 */
public interface Subscriber
{
    void onTrace( Event event );
    void onTrace( Event event, Throwable throwable );
    void onDebug( Event event );
    void onDebug( Event event, Throwable throwable );
    void onInfo( Event event );
    void onInfo( Event event, Throwable throwable );
    void onWarn( Event event );
    void onWarn( Event event, Throwable throwable );
    void onError( Event event );
    void onError( Event event, Throwable throwable );
}
