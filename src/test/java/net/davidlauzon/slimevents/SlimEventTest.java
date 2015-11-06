package net.davidlauzon.slimevents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jcabi.matchers.RegexMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by david on 15-11-06.
 */
public class SlimEventTest
{
    private EventsRegistry slimevents;
    private SubstriberMock subscriber;


    @Before public void setUp() throws Exception
    {
        subscriber  = new SubstriberMock();
        slimevents  = new EventsRegistry();
        slimevents.subscribe( subscriber );
    }

    @After public void tearDown() throws Exception
    {
        subscriber  = null;
        slimevents  = null;
    }


    @Test public void testBroadcastLevel()
    {
        // ERROR > WARN > INFO > DEBUG > TRACE
        slimevents.createRootEvent("Test Broadcast").error();
        assertThat(subscriber.getLastMessage(), startsWith("ERROR"));

        slimevents.createRootEvent("Test Broadcast").warn();
        assertThat( subscriber.getLastMessage(), startsWith("WARN") );

        slimevents.createRootEvent("Test Broadcast").info();
        assertThat( subscriber.getLastMessage(), startsWith("INFO") );

        slimevents.createRootEvent("Test Broadcast").debug();
        assertThat( subscriber.getLastMessage(), startsWith("DEBUG") );

        slimevents.createRootEvent("Test Broadcast").trace();
        assertThat( subscriber.getLastMessage(), startsWith("TRACE") );
    }

    @Test public void testStopDuration()
    {
        SlimEvent event;

        event = slimevents.createRootEvent("TestStopDuration").info();
        assertThat( subscriber.getLastMessage(), containsString("started") );

        event.stop().info();
        assertThat( subscriber.getLastMessage(), containsPattern("ended in [0-9.]+s") );
    }

    @Test public void testEventAttributes()
    {
        SlimEvent event;

        event = slimevents.createRootEvent("TestEventAttribute")
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
        SlimEvent eventParent;
        SlimEvent eventChild1;
        SlimEvent eventChild2;
        SlimEvent eventChild2Child1;
        SlimEvent eventChild2Child2;
        SlimEvent eventChild2Child3;

        eventParent = slimevents.createRootEvent("Request").attr("ACTION", "Person.Update").info();

        eventChild1 = eventParent.createChild("JSON Parsing").count("JSON.BYTES", 4000).info();
        Thread.sleep(1);            // Expensive computation / external system
        eventChild1.stop().info();

        eventChild2       = eventParent.createChild("Resource processing").info();
        eventChild2Child1 = eventChild2.createChild("SQL").attr("QUERY","SELECT FROM ...").info();
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