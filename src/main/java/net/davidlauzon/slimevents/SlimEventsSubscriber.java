package net.davidlauzon.slimevents;

import net.davidlauzon.slimevents.SlimEvent;

/**
 * Created by david on 15-11-05.
 */
public interface SlimEventsSubscriber
{
    void onTrace(SlimEvent event);
    void onDebug(SlimEvent event);
    void onInfo(SlimEvent event);
    void onWarn(SlimEvent event);
    void onError(SlimEvent event);
}
