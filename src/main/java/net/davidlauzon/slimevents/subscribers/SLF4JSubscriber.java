package net.davidlauzon.slimevents.subscribers;

import net.davidlauzon.slimevents.SlimEvent;
import net.davidlauzon.slimevents.SlimEventsSubscriber;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 15-11-05.
 */
public class SLF4JSubscriber implements SlimEventsSubscriber
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
        log.debug(formatEvent(event));
    }

    @Override
    public void onInfo(SlimEvent event) {
        log.info(formatEvent(event));
    }

    @Override
    public void onWarn(SlimEvent event) {
        log.warn(formatEvent(event));
    }

    @Override
    public void onError(SlimEvent event) {
        log.error(formatEvent(event));
    }

    protected String formatEvent(SlimEvent event)
    {
        Map<String,String> attributesMap = event.attributes();
        Map<String,Long>   countersMap   = event.counters();;

        List<String>    attributesList = new ArrayList<>( attributesMap.size() );
        List<String>    countersList   = new ArrayList<>( countersMap.size() );

        for (Map.Entry<String,String> entry : attributesMap.entrySet())
        {
            attributesList.add( entry.getKey() + "=\"" + entry.getValue() + '"' );
        }

        for (Map.Entry<String,Long> entry : countersMap.entrySet())
        {
            countersList.add( entry.getKey() + "=\"" + entry.getValue() + '"' );
        }

        switch (event.state())
        {
            case STARTED:
                return String.format("%s started. %s  %s",
                        event.eventName(),
                        attributesList,
                        countersList);

            case ENDED:
                return String.format("%s ended in %.3fs. %s, %s",
                        event.eventName(),
                        event.durationInMS() / 1000f,
                        attributesList,
                        countersList);

            default:
                // State NEW
                return null;
        }
    }
}
