package net.davidlauzon.slimevents.subscriber;

import com.google.common.io.BaseEncoding;
import net.davidlauzon.slimevents.event.Event;
import net.davidlauzon.slimevents.event.attribute.Attribute;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 15-11-06.
 */
public class AbstractSubscriber
{
    static public final String PROVENANCE_SEP = ".";


    protected String formatEvent(Event event) {
        Map<String, Attribute> attributesMap;
        List<String> attributesList;

        attributesMap  = event.attributes();
        attributesList = new ArrayList<>(attributesMap.size());

        Attribute attribute;
        String attributeName;

        for ( Map.Entry<String,Attribute> entry : attributesMap.entrySet() )
        {
            attributeName = entry.getKey();
            attribute     = entry.getValue();

            switch (attribute.type())
            {
                case Text:
                    attributesList.add( entry.getKey() + "=\"" + attribute.stringValue() + '"' );
                    break;

                case Counter:
                    attributesList.add( entry.getKey() + "=" + attribute.stringValue() );
                    break;

                default:
                    throw new RuntimeException("Unsupported attribute type: '" + attribute.type() + "'");
            }
        }

        switch (event.state()) {
            case STARTED:
                attributesList.add( "Provenance=\"" + getEventProvenance(event) + '"' );
                return String.format("%s%s started. %s",
                        getDepthPrefix(event),
                        event.eventName(),
                        attributesList
                );

            case ENDED:
                attributesList.add( "Provenance=\"" + getEventProvenance(event) + '"' );
                return String.format("%s%s ended in %.3fs. %s",
                        getDepthPrefix(event),
                        event.eventName(),
                        event.durationInMS() / 1000f,
                        attributesList
                );

            default:
                // State NEW
                return null;
        }
    }


    protected String getDepthPrefix( Event event )
    {
        // Returns a string of N spaces length, where N is the depth level of the event to the root event
        return CharBuffer.allocate( event.depth() ).toString().replace( '\0', ' ' );
    }

    protected String getEventProvenance( Event event )
    {
        StringBuilder provenance = new StringBuilder().append( getObjectHexIdentity(event) );
        Event parent = event.parent();

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
