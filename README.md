# LogShaper
Lightweight library (.jar < 20KB) allowing to log events and metrics in a structured and generic way to facilitate their automated analysis.

## Why LogShaper ?
Every respectable application log the progress of its internal state to a journal, typically one phrase per event in a flat file.
This allows a human to investigate the logs to debug a potential problem with the application.
While this approach is the easiest to implement, it raises some challenges. Here's the goals that LogShaper aims to address:

### Goal 1: Multi-tenancy
As the application is being used by an increasing number of users concurrently, it becomes more difficult to isolate the events of a particular user from the other events, and thus slows down the debugging.

### Goal 2: Event Traceability
In a large application with many layers of complex business rules, it may be difficult to see which is the causal event of another event.
While LogShaper is NOT a suitable replacement for a good profiler; it does offer an elegant way to track the traceability of related events within a single server request.

### Goal 3: Many requests per user interaction
In many modern client-server architecture (especially in REST-based single-page applications), the server offer granular API services to provide maximum flexibility.
This means that a single user interaction (e.g. mouse click, etc.) may results in many API requests being made to the server.
Taken individually, each request may be working fine, but taken as whole, may cause a user interaction to be slow.
Adding the multi-tenancy and traceability challenges into the mix, the debugging is even more difficult.

### Goal 4: Performance monitoring and management
When a specific [level of service](https://en.wikipedia.org/wiki/Level_of_service) is required, a system must monitor its health via various metrics like response latency, hardware resource usage, etc.
Some [APM](https://en.wikipedia.org/wiki/Application_performance_management) tools like New Relics and AppDynamics combines metrics from multiple components (client, server, proxy, database, cache server, etc.) to do just that, but as this library was being created, they didn't have good support for single-page applications.
LogShaper intends to leverage the logging system to extract basic metrics by _shaping_ the logs in a standard way.
Therefore making it easier to build your own APM with tools like [LogStash](https://www.elastic.co/products/logstash).

### Goal 5: "Short-and-sweet" API
LogShaper aims to provide a short API that is convenient and easy to use.


## What is LogShaper ?
Here's a sneak peak of what LogShaper's output looks like:

	 INFO Request started. [HTTP.Verb="PUT", URL="/people/1", Provenance="CIB-JQ"]
	DEBUG  JSON.Decode started. [JSON.Decoded.Bytes=4096, Provenance="CIB-JQ.KjBG2g"]
	DEBUG  JSON.Decode ended in 0.006s. [JSON.Decoded.Bytes=4096, Provenance="CIB-JQ.KjBG2g"]
	 INFO  DB.Transaction started. [Isolation.Level="Read committed", Provenance="CIB-JQ.KgmBKQ"]
	 INFO   DB.Read started. [QUERY="SELECT FROM ...", Provenance="CIB-JQ.KgmBKQ.GY4oZw"]
	 INFO   DB.Read ended in 0.014s. [QUERY="SELECT FROM ...", DB.Read.NbRows=3, Provenance="CIB-JQ.KgmBKQ.GY4oZw"]
	 INFO   DB.Write started. [QUERY="UPDATE ...", Provenance="CIB-JQ.KgmBKQ.EvQMJQ"]
	 INFO   DB.Write ended in 0.031s. [QUERY="UPDATE ...", DB.Write.NbRows=2, Provenance="CIB-JQ.KgmBKQ.EvQMJQ"]
	 INFO   BusinessRule started. [Rule="UpdateBudget", Provenance="CIB-JQ.KgmBKQ.OtqeNw"]
	 INFO    DB.Write started. [QUERY="UPDATE ...", Provenance="CIB-JQ.KgmBKQ.OtqeNw.XLxQjA"]
	 INFO    DB.Write ended in 0.036s. [QUERY="UPDATE ...", DB.Write.NbRows=4, Provenance="CIB-JQ.KgmBKQ.OtqeNw.XLxQjA"]
	 INFO   BusinessRule ended in 0.038s. [Rule="UpdateBudget", DB.Write.MS=36, DB.Write.NbRows=4, Provenance="CIB-JQ.KgmBKQ.OtqeNw"]
	 INFO  DB.Transaction ended in 0.083s. [Isolation.Level="Read committed", DB.Read.MS=14, DB.Read.NbRows=3, DB.Write.MS=67, DB.Write.NbRows=6, BusinessRule.MS=38, Provenance="CIB-JQ.KgmBKQ"]
	TRACE  ThirdPartyApp started. [Provenance="CIB-JQ.NBmGbA"]
	TRACE  ThirdPartyApp ended in 0.009s. [Provenance="CIB-JQ.NBmGbA"]
	DEBUG  JSON.Encode started. [Provenance="CIB-JQ.Bj4x7g"]
	DEBUG  JSON.Encode ended in 0.007s. [JSON.Encoded.Bytes=2134, Provenance="CIB-JQ.Bj4x7g"]
	 INFO Request ended in 0.108s. [HTTP.Verb="PUT", URL="/people/1", JSON.Decoded.Bytes=4096, JSON.Decode.MS=6, DB.Read.MS=14, DB.Read.NbRows=3, DB.Write.MS=67, DB.Write.NbRows=6, BusinessRule.MS=38, DB.Transaction.MS=83, ThirdPartyApp.MS=9, JSON.Encode.MS=7, JSON.Encoded.Bytes=2134, HTTP.Status="200", Provenance="CIB-JQ"]

Note: the code that generated the results above can be found in the [`LogShaperTest::testEventHierarchy()` method](src/test/java/net/davidlauzon/logshaper/LogShaperTest.java).


## Basic concepts

![LogShaper overview](http://yuml.me/diagram/scruffy/class/[Event|startDatetime;stopDatetime;isPonctual]++-*[Attribute], [Event]parent-->[Event], [Journal]++-*[Event], [Journal]<>-*[Subscriber|logLevel])

### Log Event
- An event has **2 main states**: `started` and `ended`.
- An event can have a different set of attributes for each state;
- The **attributes are displayed in the same order which they were called**;
- Each event, belongs to a **parent event**, except the **root events** which have no parents.
- The traceability of the events is computed automatically (_see the `Provenance` attribute above_);
- A **counter* is a special type of attribute which is cumulative, which means that its value is propagated and aggregated up in its parent's hierarchy (_see the **DB.Write.NbRows** attribute above_);
- The **duration counters** are computed automatically;
- A **ponctual event** cannot be started, nor stopped since it has a duration of 0 seconds. (e.g. an `Exception`); 

### Journal
- An event is logged into a **journal**;
- An application can have multiple journals;

### Subscriber
- A client can be notified of the events by **subscribing** to a journal;
- The class `SLF4JSubscriber` provides **compatibility with existing logging framework** via the SLF4J interface (logback, log4j, etc.);
- Support for the **standard log levels**: `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`;
- If needed to capture the events in a custom data store, you can write your custom handling by implementing the `LogSubscriber` interface;


## Getting started

First, configure the default journal when your application starts

	Logger       logger  = LoggerFactory.getLogger("logshaper");
	EventJournal journal = new SimpleJournal();
	
	journal.subscribe( new SLF4JSubscriber(logger) );
	
	LogShaper.setDefaultJournal( journal );

Then, in your controller:

	public String get() {
		LogEvent requestEvent = LogShaper.defaultJournal().newRootEvent("Request")
		    .attr("HTTP.Verb", "PUT")
		    .attr("URL", "/people/1")
		    .publishInfo();
	
		LogEvent jsonDecodeEvent = requestEvent.newChildEvent("JSON.Decode")
	        .count("JSON.Decoded.Bytes", 4096)
	        .publishDebug();
        // Expensive computation / external system
        jsonDecodeEvent.stop().publishDebug();
		
		requestEvent.stop()
			.attr("HTTP.Status", "200")
			.publishInfo();
		
		return result;
	}

Note that you could choose to publish the start and stop state at different log level, or only publish one of them.


## Going further
As you may have noticed, the SimpleJournal API above requires the event to be carried as a Data Transfer Object (DTO) through all the layers of your architecture.

Alternatively, you look into the [`ThreadRelativeJournal` implementation](src/main/java/net/davidlauzon/logshaper/journal/ThreadRelativeJournal.java).
This journal will keep track of the current event for you, without the need of using a DTO anywhere in your code.
For exemple:

	LogShaper.setDefaultJournal( new ThreadRelativeJournal() );
	
	LogShaper.defaultJournal().newChildEvent("My child event");
