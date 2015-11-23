package net.davidlauzon.logshaper.event;

/**
 * Created by david on 15-11-07.
 */
public enum EventState
{
    /**
     * An event which has NOT yet started
     */
    NEW,

    /**
     * An event which has started
     */
    STARTED,

    /**
     * An event which has ended
     */
    ENDED
}
