package net.davidlauzon.slimevents;

import net.davidlauzon.slimevents.subscribers.AbstractSubscriber;

/**
 * Created by david on 15-11-06.
 */
public class SubstriberMock extends AbstractSubscriber implements Subscriber
{
    private String lastMessage;


    @Override
    public void onTrace(Event event)
    {
        publish("TRACE " + formatEvent(event));
    }

    @Override
    public void onDebug(Event event)
    {
        publish("DEBUG " + formatEvent(event));
    }

    @Override
    public void onInfo(Event event)
    {
        publish( "INFO " + formatEvent(event));
    }

    @Override
    public void onWarn(Event event)
    {
        publish( "WARN " + formatEvent(event));
    }

    @Override
    public void onError(Event event)
    {
        publish( "ERROR " + formatEvent(event));
    }


    private void publish( String msg )
    {
        // Keep the last message so we can validate the output in the tests
        lastMessage = msg;
        System.out.println( msg );
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
