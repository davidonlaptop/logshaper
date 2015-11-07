# SlimEvents
Lightweight library allowing to log application events and metrics in a structured and generic way to facilitate their automated analysis using publish-subscribe model

## What is SlimEvents ?
Every respectable application log its internal events to a journal, typically one phrase per event in a flat file.
While this approach is simple to implement, it makes it harder afterward to parse the text output automatically.
SlimEvents address these shortcomings by providing an easy-to-use API to standardize the log output while remaining flexible.

The central concept is an Event. An Event has 2 subevents: "Start Event" and "End Event", and each of them can have multiple attribute.

## Design goals
* Designed for a REST web services application in mind, but could be used as well for an RPC application;
* Must be able to combine multiple requests to a single user action;
* Fast learning curve;
* Extensibility;

Goal: profiling + logging
Profiling is hard in client-server app

Not thread-safe. So 1 EventRegistry per thread.

Some attribute should be cumulative (e.g. DB Time)
Other attributes should not (e.g. status code)

Root events should be able to show accumulated

Provide metrics during tests

Avoid 

Problem 1: How can Event can aggregate recursively multiple events ?
  => use case main Request
  => nice to have if intermediate also aggregates
        => what if we track current event in Data Transfer Object ?
            => if we decide to support without DTO, then we could use child() property of event
Problem 2: How can we stop an Event (two events vs single event)
    => must be able to tell if 2 events have same name
    => must be able to start 2 events with same name in parallel
    => must be able to start / stop event in 2 different methods
Problem 3: How to access an Event from a thread ? e.g. BudgetAverage in a thread ?
  => easy if it receives a Data Transfer Object

Problem 4: Avoid circular depency
    => Chaque enfant a un lien vers son parent
    =
    => Every Event is the child of its parent

Feature 5: Automatic child event aggregation
    => "magic counter"

SlimEvent event = parentEvent.startChildEvent("Name").attribute("Action","Debtor.Show").broadcast( INFO )
SlimEvent event = parentEvent.startChildEvent("Name").attribute("Action","Debtor.Show").broadcastInfo()
SlimEvent event = parentEvent.startChildEvent("Name").attribute("Action","Debtor.Show").publishInfo()
SlimEvent event = parentEvent.startChildEvent("Name").attribute("Action","Debtor.Show").publish().info()
SlimEvent event = parentEvent.addChild("Name").attribute("Action","Debtor.Show").info()

doSomething( event );
// ... do something

event.counter("DB.Time",143).stopAndBroadcast( INFO )
// or if we don't want to broadcast the event
event.stopAndBroadcast( INFO )
// Stops returns the parent
    => How do you broadcast the event then?

You could choose to only broadcast the start event(), or only the stop() event

TODO: Should "Stop" events always include the attributes/counters of the start event ?

TODO: faire methode statique pour acceder a SlimEvent (E.g. SlimEvents.setDefaultRegistry, SlimEvents.log...)
TODO: handle exceptions
TODO: publish jar to artifactory
TODO: attach source
TODO: sign pgp
TODO: use a better unique id for root event (e.g. 128-bit or 256-bit id)

TODO: rename method with publish()

TODO: name: MetricsMachine ?