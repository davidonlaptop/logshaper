package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.Event;
import org.junit.*;

import java.io.IOError;
import java.io.IOException;
import java.util.IllegalFormatException;

import static com.jcabi.matchers.RegexMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by david on 15-11-06.
 */
public class LogShaperTest
{
    static private SubstriberMock subscriber;


    @BeforeClass static public void setUp() throws Exception
    {
        subscriber  = new SubstriberMock();
        LogShaper.getDefaultJournal().subscribe( subscriber  );
    }


    @AfterClass static public void tearDown() throws Exception
    {
        subscriber  = null;
    }


    @Test public void testBroadcastLevel()
    {
        // ERROR > WARN > INFO > DEBUG > TRACE
        LogShaper.createRootEvent("TestBroadcast").publishError();
        assertThat(subscriber.getLastMessage(), startsWith("ERROR"));

        LogShaper.createRootEvent("TestBroadcast").publishWarn();
        assertThat( subscriber.getLastMessage(), startsWith("WARN") );

        LogShaper.createRootEvent("TestBroadcast").publishInfo();
        assertThat( subscriber.getLastMessage(), startsWith("INFO") );

        LogShaper.createRootEvent("TestBroadcast").publishDebug();
        assertThat( subscriber.getLastMessage(), startsWith("DEBUG") );

        LogShaper.createRootEvent("TestBroadcast").publishTrace();
        assertThat( subscriber.getLastMessage(), startsWith("TRACE") );
    }


    @Test public void testStopDuration()
    {
        Event event;

        event = LogShaper.createRootEvent("TestStopDuration").publishInfo();
        assertThat( subscriber.getLastMessage(), containsString("started") );

        event.stop().publishInfo();
        assertThat( subscriber.getLastMessage(), containsPattern("ended in [0-9.]+s") );
    }


    @Test public void testEventAttributes()
    {
        Event event;

        event = LogShaper.createRootEvent("TestEventAttribute")
                .attr("KEY1", "val1").attr("KEY2", "val2")
                .publishInfo();
        assertThat( subscriber.getLastMessage(), allOf(
                containsString("KEY1=\"val1\""),
                containsString("KEY2=\"val2\"")
        ));

        event.stop()
                .attr("KEY3", "stopped")
                .publishInfo();
        assertThat( subscriber.getLastMessage(), allOf(
                containsString("KEY1=\"val1\""),
                containsString("KEY2=\"val2\""),
                containsString("KEY3=\"stopped\"")
        ));
    }


    @Test public void testEventHierarchy() throws InterruptedException {
        Event eventParent;
        Event eventChild1;
        Event eventChild2;
        Event eventChild2Child1;
        Event eventChild2Child2;
        Event eventChild2Child3;

        eventParent = LogShaper.createRootEvent("Request").attr("ACTION", "Person.Update").publishInfo();

        eventChild1 = eventParent.createChild("JSON Parsing").count("JSON.BYTES", 4000).publishInfo();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild1.stop().publishInfo();

        eventChild2       = eventParent.createChild("Resource processing").publishInfo();
        eventChild2Child1 = eventChild2.createChild("SQL").attr("QUERY", "SELECT FROM ...").publishInfo();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child1.stop().publishInfo();
        eventChild2Child2 = eventChild2.createChild("BIRT").publishInfo();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child2.stop().publishInfo();
        eventChild2.stop().publishInfo();
        eventChild2Child3 = eventChild2.createChild("SQL").attr("QUERY", "UPDATE ...").publishInfo();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child3.stop().publishInfo();

        eventParent.stop().publishInfo();

        try {
            throw new IOException("my message", new IllegalArgumentException("my illegal argument"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test public void testPonctualEvent()
    {
        Event event;

        event = LogShaper.createRootEvent("TestPonctualEvent")
                .ponctualEvent()
                .attr("KEY1", "val1").attr("KEY2", "val2")
                .publishInfo();
        assertThat( subscriber.getLastMessage(), allOf(
                containsString("KEY1=\"val1\""),
                containsString("KEY2=\"val2\""),
                containsString("occured")
        ));


        event = LogShaper.createRootEvent("TestPonctualEventParent").attr("KEY1", "val1").publishInfo();
        event.createChild("TestPonctualEventChild").ponctualEvent().publishInfo();
        assertThat(subscriber.getLastMessage(), containsString("occured") );
        event.stop().publishInfo();
    }


    @Test public void testThrowableEvent()
    {
        Event event;

        event = LogShaper.createRootEvent("TestThrowableEvent")
                .attr("KEY1", "val1").attr("KEY2", "val2")
                .publishInfo();

        event.createChild("TestThrowableEventException", new Throwable("Something got wrong")).publishInfo();

        event.stop();
    }
}