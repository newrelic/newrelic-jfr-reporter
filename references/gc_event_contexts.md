## jdk.ThreadAllocationStatistics
  ```
  startTime = 23:15:34.910
  allocated = 1.5 GB
  thread = "main" (javaThreadId = 1)
```

## jdk.G1HeapRegionInformation
```
  startTime = 23:15:34.911
  index = 0
  type = "Starts Humongous"
  start = 0x83600000
  used = 1.0 MB
```
## jdk.ObjectAllocationInNewTLAB
```
  startTime = 23:15:36.009
  objectClass = java.lang.Object[] (classLoader = bootstrap)
  allocationSize = 24 bytes
  tlabSize = 512.0 kB
  eventThread = "main" (javaThreadId = 1)
  stackTrace = [
    java.util.concurrent.atomic.AtomicReferenceArray.<init>(int) line: 68
    com.newrelic.agent.deps.com.google.common.cache.LocalCache$Segment.newEntryArray(int) line: 1964
    com.newrelic.agent.deps.com.google.common.cache.LocalCache$Segment.<init>(LocalCache, int, long, AbstractCache$StatsCounter) line: 1941
    com.newrelic.agent.deps.com.google.common.cache.LocalCache.createSegment(int, long, AbstractCache$StatsCounter) line: 1732
    com.newrelic.agent.deps.com.google.common.cache.LocalCache.<init>(CacheBuilder, CacheLoader) line: 307
    ...
  ]
```

## jdk.G1HeapRegionTypeChange
```
  startTime = 23:15:36.011
  index = 79
  from = "Free"
  to = "Eden"
  start = 0x88500000
  used = 0 bytes
```

## jdk.ObjectAllocationOutsideTLAB 
```
  startTime = 23:15:36.011
  objectClass = byte[] (classLoader = bootstrap)
  allocationSize = 8.4 kB
  eventThread = "main" (javaThreadId = 1)
  stackTrace = [
    jdk.internal.jimage.BasicImageReader.getBufferBytes(ByteBuffer) line: 339
    jdk.internal.jimage.BasicImageReader.getResource(ImageLocation) line: 395
    jdk.internal.jimage.ImageReader.getResource(ImageLocation) line: 189
    sun.net.www.protocol.jrt.JavaRuntimeURLConnection$1.getInputStream() line: 110
    sun.net.www.protocol.jrt.JavaRuntimeURLConnection.getInputStream() line: 141
    ...
  ]
```
## jdk.AllocationRequiringGC 
```
  startTime = 23:15:37.125
  gcId = 23
  size = 2.0 kB
  eventThread = "main" (javaThreadId = 1)
  stackTrace = [
    com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode.visitLineNumber(int, Label) line: 510
    com.newrelic.agent.deps.org.objectweb.asm.Label.accept(MethodVisitor, boolean) line: 360
    com.newrelic.agent.deps.org.objectweb.asm.ClassReader.readCode(MethodVisitor, Context, int) line: 1824
    com.newrelic.agent.deps.org.objectweb.asm.ClassReader.readMethod(ClassVisitor, Context, int) line: 1284
    com.newrelic.agent.deps.org.objectweb.asm.ClassReader.accept(ClassVisitor, Attribute[], int) line: 688
    ...
  ]
  ```
## jdk.GCHeapSummary 
  ```
  startTime = 23:15:37.125
  gcId = 23
  when = "Before GC"
  heapSpace = {
    start = 0x83600000
    committedEnd = 0x8FE00000
    committedSize = 200.0 MB
    reservedEnd = 0x100000000
    reservedSize = 1.9 GB
  }
  heapUsed = 197.8 MB
```  
## jdk.G1HeapSummary 
  ```
  startTime = 23:15:37.125
  gcId = 23
  when = "Before GC"
  edenUsedSize = 143.0 MB
  edenTotalSize = 153.0 MB
  survivorUsedSize = 14.0 MB
  numberOfRegions = 200
```
## jdk.GCPhasePauseLevel1 
  ```
  startTime = 23:15:37.165
  duration = 0.00616 ms
  gcId = 23
  name = "Reconsider SoftReferences"
  ```
## jdk.GCPhasePauseLevel2 
  ```
  startTime = 23:15:37.165
  duration = 0.0738 ms
  gcId = 23
  name = "Notify Soft/WeakReferences"
```
## jdk.GCReferenceStatistics 
  ```
  startTime = 23:15:37.166
  gcId = 23
  type = "Soft reference"
  count = 0
```
## jdk.G1EvacuationYoungStatistics 
  ```
  startTime = 23:15:37.168
  statistics = {
    gcId = 23
    allocated = 9.1 MB
    wasted = 3.3 kB
    used = 8.6 MB
    undoWaste = 0 bytes
    regionEndWaste = 0 bytes
    regionsRefilled = 10 bytes
    directAllocated = 454.7 kB
    failureUsed = 0 bytes
    failureWaste = 0 bytes
  }
  ```
## jdk.G1EvacuationOldStatistics 
  ```
  startTime = 23:15:37.168
  statistics = {
    gcId = 23
    allocated = 3.0 MB
    wasted = 25.5 kB
    used = 2.7 MB
    undoWaste = 0 bytes
    regionEndWaste = 0 bytes
    regionsRefilled = 3 bytes
    directAllocated = 0 bytes
    failureUsed = 0 bytes
    failureWaste = 0 bytes
  }
  ```
## jdk.G1MMU  
  ```
  startTime = 23:15:37.168
  gcId = 23
  timeSlice = 201 ms
  gcTime = 42.0 ms
  pauseTarget = 200 ms
```
## jdk.G1BasicIHOP 
```
  startTime = 23:15:37.168
  gcId = 23
  threshold = 95.8 MB
  thresholdPercentage = 45.00%
  targetOccupancy = 213.0 MB
  currentOccupancy = 53.3 MB
  recentMutatorAllocationSize = 3.0 MB
  recentMutatorDuration = 4.18 s
  recentAllocationRate = 725.1 kB/s
  lastMarkingDuration = 0 s
```  
## jdk.G1AdaptiveIHOP 
  ```
  startTime = 23:15:37.168
  gcId = 23
  threshold = 95.8 MB
  thresholdPercentage = 47.37%
  ihopTargetOccupancy = 202.3 MB
  currentOccupancy = 122.0 MB
  additionalBufferSize = 53.3 MB
  predictedAllocationRate = 17.8 MB/s
  predictedMarkingDuration = 0 s
  predictionActive = false
```
## jdk.GCPhasePause 
  ```
  startTime = 23:15:37.125
  duration = 44.5 ms
  gcId = 23
  name = "GC Pause"
  ```
## jdk.G1GarbageCollection 
  ```
  startTime = 23:15:37.125
  duration = 44.5 ms
  gcId = 23
  type = "Initial Mark"
```
## jdk.GCPhaseConcurrent 
```
  startTime = 23:15:37.170
  duration = 0.0564 ms
  gcId = 24
  name = "Concurrent Clear Claimed Marks"
```

## jdk.MetaspaceGCThreshold
``` 
  startTime = 23:15:37.247
  oldValue = 35.6 MB
  newValue = 54.2 MB
  updater = "compute_new_size"
```

## jdk.ObjectCountAfterGC 
```
  startTime = 23:15:37.266
  gcId = 24
  objectClass = boolean[] (classLoader = bootstrap)
  count = 1078
  totalSize = 260.8 kB
```