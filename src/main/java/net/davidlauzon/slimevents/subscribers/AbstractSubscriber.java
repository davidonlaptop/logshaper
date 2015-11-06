package net.davidlauzon.slimevents.subscribers;

import com.google.common.io.BaseEncoding;
import net.davidlauzon.slimevents.SlimEvent;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 * Created by david on 15-11-06.
 */
public class AbstractSubscriber
{
    static public final String PROVENANCE_SEP = ".";


    protected String formatEvent(SlimEvent event) {
        Map<String, String> attributesMap = event.attributes();
        Map<String, Long> countersMap = event.counters();
        ;

        List<String> attributesList = new ArrayList<>(attributesMap.size());
        List<String> countersList = new ArrayList<>(countersMap.size());

        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            attributesList.add(entry.getKey() + "=\"" + entry.getValue() + '"');
        }

        for (Map.Entry<String, Long> entry : countersMap.entrySet()) {
            countersList.add(entry.getKey() + "=" + entry.getValue());
        }

        switch (event.state()) {
            case STARTED:
                return String.format("%s%s started. %s  %s  Provenance=\"%s\"",
                        getDepthPrefix(event),
                        event.eventName(),
                        attributesList,
                        countersList,
                        getEventProvenance(event)
                );
            case ENDED:
                return String.format("%s%s ended in %.3fs. %s %s  Provenance=\"%s\"",
                        getDepthPrefix(event),
                        event.eventName(),
                        event.durationInMS() / 1000f,
                        attributesList,
                        countersList,
                        getEventProvenance(event)
                );
            default:
                // State NEW
                return null;
        }
    }


    protected String getDepthPrefix( SlimEvent event )
    {
        // Returns a string of N spaces length, where N is the depth level of the event to the root event
        return CharBuffer.allocate( event.depth() ).toString().replace( '\0', ' ' );
    }

    protected String getEventProvenance( SlimEvent event )
    {
        StringBuilder provenance = new StringBuilder().append( getObjectHexIdentity(event) );
        SlimEvent parent = event.parent();

        while (parent != null)
        {
            provenance.insert(0, getObjectHexIdentity(parent) + PROVENANCE_SEP );

            parent = parent.parent();
        }
        return provenance.toString();
    }

    protected String getObjectHexIdentity( Object object )
    {
        // Using the object's hashCode as the unique identifier
        // ----------------------------------------------------
        // FIXME: Hashes are not unique, hence they are not apropriate for uniqueId.
        //
        // Since this hash is a 32-bit integer, there is 25% probability of collisions if there is more than
        // 50,000 java objects which is quite likely. Using a random 64-bits or 128-bits integer would decrease this
        // probability:
        //
        // 64bits  :  p = 10^-9   with  190,000 objects   ;   p = 0.1%  with 190M objects
        // 128bits :  p = 10^-18  with  26G objects
        //
        // https://en.wikipedia.org/wiki/Birthday_problem#Probability_table

        // System.identityHashCode()
        // -------------------------
        // Returns the same hash code for the given object as would be returned by the default method hashCode(),
        // whether or not the given object's class overrides hashCode(). The hash code for the null reference is zero.
        // http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#identityHashCode(java.lang.Object)
        //
        // This is NOT the memory address:
        //      http://stackoverflow.com/a/20680667/359793

        // Base64
        // ------
        // Using Google Guava since it uses RFC 4648's version of Base64 which uses '-', and '_' characters
        // instead of '+' and '/'.
        // This is supported natively in Java 8: http://docs.oracle.com/javase/8/docs/api/java/util/Base64.html

        int    id       = System.identityHashCode( object );
        byte[] bytes    = ByteBuffer.allocate(4).putInt( id ).array();
        String base64   = BaseEncoding.base64Url().omitPadding().encode( bytes );

        return base64;
    }
}
