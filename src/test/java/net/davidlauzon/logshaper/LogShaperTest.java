package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.Event;
import org.junit.*;

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
        LogShaper.getDefaultRegistry().subscribe( subscriber  );
    }

    @AfterClass static public void tearDown() throws Exception
    {
        subscriber  = null;
    }


    @Test public void testBroadcastLevel()
    {
        // ERROR > WARN > INFO > DEBUG > TRACE
        LogShaper.createRootEvent("TestBroadcast").error();
        assertThat(subscriber.getLastMessage(), startsWith("ERROR"));

        LogShaper.createRootEvent("TestBroadcast").warn();
        assertThat( subscriber.getLastMessage(), startsWith("WARN") );

        LogShaper.createRootEvent("TestBroadcast").info();
        assertThat( subscriber.getLastMessage(), startsWith("INFO") );

        LogShaper.createRootEvent("TestBroadcast").debug();
        assertThat( subscriber.getLastMessage(), startsWith("DEBUG") );

        LogShaper.createRootEvent("TestBroadcast").trace();
        assertThat( subscriber.getLastMessage(), startsWith("TRACE") );
    }

    @Test public void testStopDuration()
    {
        Event event;

        event = LogShaper.createRootEvent("TestStopDuration").info();
        assertThat( subscriber.getLastMessage(), containsString("started") );

        event.stop().info();
        assertThat( subscriber.getLastMessage(), containsPattern("ended in [0-9.]+s") );
    }

    @Test public void testEventAttributes()
    {
        Event event;

        event = LogShaper.createRootEvent("TestEventAttribute")
                .attr("KEY1", "val1").attr("KEY2", "val2")
                .info();
        assertThat( subscriber.getLastMessage(), allOf(
                containsString("KEY1=\"val1\""),
                containsString("KEY2=\"val2\"")
        ));

        event.stop()
                .attr("KEY3", "stopped")
                .info();
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

        eventParent = LogShaper.createRootEvent("Request").attr("ACTION", "Person.Update").info();

        eventChild1 = eventParent.createChild("JSON Parsing").count("JSON.BYTES", 4000).info();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild1.stop().info();

        eventChild2       = eventParent.createChild("Resource processing").info();
        eventChild2Child1 = eventChild2.createChild("SQL").attr("QUERY", "SELECT FROM ...").info();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child1.stop().info();
        eventChild2Child2 = eventChild2.createChild("BIRT").info();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child2.stop().info();
        eventChild2.stop().info();
        eventChild2Child3 = eventChild2.createChild("SQL").attr("QUERY", "UPDATE ...").info();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild2Child3.stop().info();

        eventParent.stop().info();
    }
}