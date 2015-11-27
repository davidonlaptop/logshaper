package net.davidlauzon.logshaper;

import net.davidlauzon.logshaper.event.LogEvent;
import org.junit.*;

import static com.jcabi.matchers.RegexMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


/**
 * Created by david on 15-11-06.
 */
public class LogShaperTest
{
    static private SubscriberMock subscriber;


    @BeforeClass static public void setUp() throws Exception
    {
        subscriber  = new SubscriberMock();
        LogShaper.defaultJournal().subscribe( subscriber  );
    }


    @AfterClass static public void tearDown() throws Exception
    {
        subscriber  = null;
    }


    @Test public void testBroadcastLevel()
    {
        // ERROR > WARN > INFO > DEBUG > TRACE
        LogShaper.newRootEvent("TestBroadcast").publishError();
        assertThat(subscriber.getLastMessage(), startsWith("ERROR"));

        LogShaper.newRootEvent("TestBroadcast").publishWarn();
        assertThat( subscriber.getLastMessage(), startsWith(" WARN") );

        LogShaper.newRootEvent("TestBroadcast").publishInfo();
        assertThat(subscriber.getLastMessage(), startsWith(" INFO"));

        LogShaper.newRootEvent("TestBroadcast").publishDebug();
        assertThat( subscriber.getLastMessage(), startsWith("DEBUG") );

        LogShaper.newRootEvent("TestBroadcast").publishTrace();
        assertThat( subscriber.getLastMessage(), startsWith("TRACE") );
    }


    @Test public void testStopDuration()
    {
        LogEvent event;

        event = LogShaper.newRootEvent("TestStopDuration").publishInfo();
        assertThat( subscriber.getLastMessage(), containsString("started") );

        event.stop().publishInfo();
        assertThat( subscriber.getLastMessage(), containsPattern("ended in [0-9.]+s") );
    }


    @Test public void testEventAttributes()
    {
        LogEvent event;

        event = LogShaper.newRootEvent("TestEventAttribute")
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


    /**
     * An example of the output you may want in your application.
     *
     * @throws InterruptedException
     */
    @Test public void testEventHierarchy() throws InterruptedException
    {
        LogEvent requestEvent;
        LogEvent jsonDecodeEvent;
        LogEvent dbTransactionEvent;
        LogEvent dbSelectEvent;
        LogEvent dbUpdateEvent;
        LogEvent businessRuleEvent;
        LogEvent dbUpdate2Event;
        LogEvent thirdPartyAppEvent;
        LogEvent jsonEncodeEvent;

        // Level: 0 (root)
        requestEvent = LogShaper.newRootEvent("Request")
                .attr("HTTP.Verb", "PUT").attr("URL", "/people/1")
                .publishInfo();

        // Level: 1
        jsonDecodeEvent = requestEvent.newChildEvent("JSON.Decode")
                .count("JSON.Decoded.Bytes", 4096)
                .publishDebug();
        Thread.sleep(5);            // Expensive computation / external system
        jsonDecodeEvent.stop().publishDebug();

        // Level: 1
        dbTransactionEvent = requestEvent.newChildEvent("DB.Transaction")
                .attr("Isolation.Level", "Read committed")
                .publishInfo();

        // Level: 2
        dbSelectEvent = dbTransactionEvent.newChildEvent("DB.Read")
                .attr("QUERY", "SELECT FROM ...")
                .publishInfo();
        Thread.sleep(12);            // Expensive computation / external system
        dbSelectEvent.stop()
                .count("DB.Read.NbRows", 3)
                .publishInfo();

        // Level: 2
        dbUpdateEvent = dbTransactionEvent.newChildEvent("DB.Write")
                .attr("QUERY", "UPDATE ...")
                .publishInfo();
        Thread.sleep(27);            // Expensive computation / external system
        dbUpdateEvent.stop()
                .count("DB.Write.NbRows", 2)
                .publishInfo();

        // Level: 2
        businessRuleEvent = dbTransactionEvent.newChildEvent("BusinessRule")
                .attr("Rule", "UpdateBudget")
                .publishInfo();

        // Level: 3
        dbUpdate2Event = businessRuleEvent.newChildEvent("DB.Write")
                .attr("QUERY", "UPDATE ...")
                .publishInfo();
        Thread.sleep(33);            // Expensive computation / external system
        dbUpdate2Event.stop()
                .count("DB.Write.NbRows", 4)
                .publishInfo();
        businessRuleEvent.stop().publishInfo();
        dbTransactionEvent.stop().publishInfo();

        // Level: 1
        thirdPartyAppEvent = requestEvent.newChildEvent("ThirdPartyApp")
                .publishTrace();
        Thread.sleep(9);            // Expensive computation / external system
        thirdPartyAppEvent.stop().publishTrace();

        // Level: 1
        jsonEncodeEvent = requestEvent.newChildEvent("JSON.Encode").publishDebug();
        Thread.sleep(5);            // Expensive computation / external system
        jsonEncodeEvent.stop()
                .count("JSON.Encoded.Bytes", 2134)
                .publishDebug();

        requestEvent.stop()
                .attr("HTTP.Status", "200")
                .publishInfo();
    }


    @Test public void testPonctualEvent()
    {
        LogEvent event;

        event = LogShaper.newRootEvent("TestPonctualEventParent")
                .attr("KEY1", "val1")
                .attr("KEY2", "val2")
                .publishInfo();

        event.newPonctualEvent("TestPonctualEventChild").publishInfo();
        assertThat(subscriber.getLastMessage(), containsString("occured") );

        event.stop().publishInfo();
    }


    @Test public void testThrowableEvent()
    {
        LogEvent event;

        event = LogShaper.newRootEvent("TestThrowableEvent")
                .attr("KEY1", "val1")
                .attr("KEY2", "val2")
                .publishInfo();

        event.newThrowableEvent(new Throwable("Something got wrong")).publishInfo();

        event.stop().publishInfo();
    }
}