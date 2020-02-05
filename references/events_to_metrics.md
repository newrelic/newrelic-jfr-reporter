# Event Mappings

This document describes how JFR Events are mapped into dimensional metrics.

## Common attributes

Common attributes are configured at the top of the SDK and apply to 
every metric emitted.  These values are static for the lifetime 
of an application:

* `host`: `InetAddress.getLocalHost().getHostName()`
* `appName`:  `NewRelic.getAgent().getConfig().getValue("app_name")`
* `metricSource`: The literal string "JFR Agent Extension"
 

## Base Attributes

The mappings below all share a set of "base attributes".  These attributes
are applied to all metrics emitted from the jfr extension.  The value of
these attributes could change during an application lifetime. 

* `agentRunId`: The agent run ID as ripped from the guts of the agent

## jdk.CPULoad

Scheduled on 1 second interval.  3 gauges:

* `new Gauge("jfr:CPULoad.jvmUser", ev.getDouble("jvmUser"), timestamp, attr)`
* `new Gauge("jfr:CPULoad.jvmSystem", ev.getDouble("jvmSystem"), timestamp, attr)`
* `new Gauge("jfr:CPULoad.machineTotal", ev.getDouble("machineTotal"), timestamp, attr)`

## jdk.ObjectAllocationInNewTLAB

A count object:
* `new Count("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + ev.getLong("allocationSize"), start, end, attr)`

with additional attributes:

* `thread`: ev.getThread("eventThread").getJavaName()
* `class`: ev.getClass("objectClass").getName()
* `tlabSize`: ev.getLong("tlabSize")

## jdk.G1GarbageCollection

A gauge object:
* `new Gauge("jfr:G1GarbageCollection.duration", ev.getDouble("duration"), timestamp, attr)`

with additional attributes:

* `G1GarbageCollection.type`: ev.getString("type")
* `gcId`: ev.getInt("gcId")

## jdk.ThreadAllocationStatistics

A gauge object:

* `new Gauge("jfr:ThreadAllocationStatistics.allocated", allocated, time, attrs)`

with additional attributes:
* `thread.id`:  thread.getId()
* `thread.name`: thread.getJavaName()
* `thread.javaId`: thread.getJavaThreadId()
* `thread.osName`: thread.getOSName()
* `thread.osId`: thread.getOSThreadId()

## jdk.G1HeapRegionInformation

A gauge object:
 
* 1new Gauge("jfr:G1HeapRegionInformation.bytesUsed", ev.getLong("used"), start, attr)`

with additional attributes:

* `index`: ev.getInt("index")
* `type`: ev.getString("type")
* `startAddress`: ev.getLong("start")

## jdk.G1HeapRegionTypeChange

A gauge object:

* `new Gauge("jfr:G1HeapRegionTypeChange.bytesUsed", ev.getLong("used"), start, attr)`

with additional attributes:

* `index`: ev.getInt("index")
* `fromType`:, ev.getValue("from")
* `toType`: ev.getValue("to")
* `startAddress`: ev.getLong("start")

## jdk.ObjectAllocationOutsideTLAB

A gauge object:

* `new Count("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + ev.getLong("allocationSize"), start, end, attr)`

with additional attributes:

* `thread.id`: thread.getId()
* `thread.name`:, thread.getJavaName()
* `class`: ev.getClass("objectClass").getName()

## jdk.AllocationRequiringGC

A gauge object:

* `new Gauge("jfr:AllocationRequiringGC.allocationSize", ev.getLong("size"), timestamp, attr)`

with additional attributes:

* `thread.id`: thread.getId()
* `thread.name`: thread.getJavaName()
* `gcId`: ev.getInt("gcId")

## jdk.GCHeapSummary

3 gauges:

* `new Gauge("jfr:GCHeapSummary.heapUsed", ev.getLong("heapUsed"), timestamp, attr),`
* `new Gauge("jfr:GCHeapSummary.heapCommittedSize", committedSize, timestamp, attr),`
* `new Gauge("jfr:GCHeapSummary.reservedSize", reservedSize, timestamp, attr)`

with additional attributes:

* `gcId`: ev.getInt("gcId")
* `when`: ev.getString("when")
* `heapStart`: ev.getValue("heapSpace").getLong("start")
* `committedEnd`: ev.getValue("heapSpace").getLong("committedEnd")
* `reservedEnd`: ev.getValue("heapSpace").getLong("reservedEnd")