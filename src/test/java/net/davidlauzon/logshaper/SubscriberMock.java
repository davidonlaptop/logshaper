package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.LogEvent;
import net.davidlauzon.logshaper.subscriber.AbstractSubscriber;
import net.davidlauzon.logshaper.subscriber.LogSubscriber;

/**
 * Created by david on 15-11-06.
 */
public class SubscriberMock extends AbstractSubscriber implements LogSubscriber
{
    private String lastMessage;


    @Override
    public void onTrace(LogEvent event)
    {
        publish("TRACE " + formatEvent(event));
    }

    @Override
    public void onTrace(LogEvent event, Throwable throwable)
    {
        onTrace(event);
        throwable.printStackTrace();
    }

    @Override
    public void onDebug(LogEvent event)
    {
        publish("DEBUG " + formatEvent(event));
    }

    @Override
    public void onDebug(LogEvent event, Throwable throwable)
    {
        onDebug(event);
        throwable.printStackTrace();
    }

    @Override
    public void onInfo(LogEvent event)
    {
        publish( " INFO " + formatEvent(event));
    }

    @Override
    public void onInfo(LogEvent event, Throwable throwable)
    {
        onInfo(event);
        throwable.printStackTrace();
    }

    @Override
    public void onWarn(LogEvent event)
    {
        publish( " WARN " + formatEvent(event));
    }

    @Override
    public void onWarn(LogEvent event, Throwable throwable)
    {
        onWarn(event);
        throwable.printStackTrace();
    }

    @Override
    public void onError(LogEvent event)
    {
        publish( "ERROR " + formatEvent(event));
    }

    @Override
    public void onError(LogEvent event, Throwable throwable)
    {
        onError(event);
        throwable.printStackTrace();
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
