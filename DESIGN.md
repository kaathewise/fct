# Design notes

## Specification

The problem specification (not included here) is, I imagine, intentionally brief and, while providing a sample of data
the application should handle, leaves a lot of room for guessing how this sample might extrapolate to a larger problem.

While designing the solution, I have come up with a few questions, that I think are significant if not for the general structure
of the project, but at least for its implementation. I would like to mention some of them, along with my tentative
answers that led me to the implementation you see here.

### 1. How many events do we aim to process, both in total and per plane?

#### 1.a. If there are many, how many of them are out-of-order, and how many "reads" are we serving?

Processing tens of millions of events would require some parallelism.

Processing billions of events or processing split across long time periods would warrant some backed-to-disk persistence layer.

Processing hundreds of events, however, allows for the simplest and the most inefficient implementation.

Indeed, if all the events are coming from status changes, as the sample shows, we can't possibly have more than 50 of them per day per plane.

Having that in mind, *I've implemented the shortest and the most inefficient solution first, but also offered a more efficient and more fun one, based on Fenwick Tree*.

### 2. How should we calculate the fuel estimates?

A naive approach is to assume that all planes have 0 fuel before the first event arrives. On the other hand, we should be able to deduce something from the fact that fuel level can never realistically be negative.

*Here I decided to follow the naive logic, to keep things simple, and because there is no hint of how to handle this consideration in the specification.*

### 3. Will every event lead to a status change, and will it be determined by event type only?

Although it is the case in the sample, uncertainty about extrapolating it to an imaginary "larger problem" led me both to *outsource association between
`Event.Type` and `FlightStatus` and to propose a more efficient solution for per-plane fuel data*.

## Performance

Now, thinking about performance, although not strictly necessary for the problem as described (see above), was a lot of fun! Algorithmically speaking, the most difficult part is calculating aggregated fuel deltas. While in some solutions one may outsource this to a database, I am actually unaware of any particular one that would be both strongly consistent and efficient, so I will limit the discussion to implementations "in code", so to speak.

The shortest solution I implemented, with a use of a `TreeMap` is even less efficient than it has to be -- it takes `O(N)` to aggregate the fuel, but still takes `O(log N)` to update, compared, say, to a simple `List` which would only need `O(1)`. Nevertheless, I chose `TreeMap`, because the code is slightly shorter and easier to understand.

Now if we want something efficient for a large number of data points, we have a few options, most notably augmented BSTs and the Fenwick Tree, all allowing for `O(log N)` inserts and reads, with a few caveats. There is also an important general optimisation idea -- *if we know that out-of-order events are pretty rare, we can store and aggregate them separately, greatly improving write performance*. The ratio between writes and reads can also affect the implementation significantly.

* BSTs, in particular, AVL/RB Trees.
  * **Pros:** most efficient, true worst-case `O(log N)`, AVL slightly better in speed, RB somewhat better in memory
  * **Cons:** most amount of code, no well-established augmentable implementation for Kotlin/JVM
* Fenwick Tree.
  * **Pros:** very short and easy to write, possible to make truly `O(log N)` (on average)
  * **Cons:** unfortunately, we can only achieve average `O(log N)` because of the average `O(1)` of the underlying `HashMap` implementation. Swapping `HashMap` for a `TreeMap` would give us `O(log^2 N)` worst (and average) case. We would also need to implement a careful "extension" of the tree to the right and to the left to achieve those asymptotically optimal results.

In the end, I chose to implement a version of the Fenwick Tree, as I didn't want to invest too much time in it. An important note there is that the updates are actually `O(log V)`, which in the case of current epoch seconds means circa 30 iterations.

## Testing

Unit/Functional tests for `InMemoryPlanesImpl` are missing due to a lack of time. `InMemoryPlanesImpl` is currently only covered by the functional tests for the whole application.

Should also add tests for idempotency of removal and insertion of events.

## Input format

Although not asked explicitly, I would like to leave some comments on the input format, as I would do if I faced this problem at work.

### Lack of record type

Unlike, say, protobuffs or proper console commands, it is impossible to expect how the record should be parsed looking just at the beginning of it. Because of that, it is hard to give a meaningful error message to the user, as we don't knnow their intention.

### Lack of timezone in timestamp

Although it looks more readable, it is important to note that without timezone specified the text `2021-03-29T10:00:00` does not actually refer to any specific moment in time. It may become a problem as different systems may well use different timezones as their default. Adding `Z` in the end makes it clear that we are using `GMT`.

### Fuel deltas

We should seek the planes to provide the absolute levels of fuel instead of deltas. Absolute levels are easily measured in planes and provide trivial recovery from missing events. A missing delta, on the other hand, messes up every one of the future estimates. Naturally, that would make the problem less challenging, but for a good reason :)
