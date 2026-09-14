# Steph User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Detecting schedule clashes

// Adding an `event` checks it against your other not-done events. If it
// overlaps one, the add is rejected and you're shown which event(s) it
// clashes with, so you don't accidentally double-book yourself.

// Only events are compared against each other -- todos and deadlines are
// never checked, and a completed (marked-done) event is never compared.

// Two events only clash if their time ranges actually overlap: one event
// ending exactly when another starts is not a clash. An event given without
// a time (a date-only `/from` or `/to`) is treated as spanning that whole
// day.

Example: `event trip /from 2019-10-15 1400 /to 2019-10-15 1600`

```
This clashes with an existing event:
  [E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)
Add '/force' to the command if you want to schedule it anyway.
```

## Overriding a clash with /force

// Add `/force` to the end of an `event` command to schedule it anyway,
// skipping the clash check entirely. This works on the very first attempt --
// you don't need to be rejected first.

Example: `event trip /from 2019-10-15 1400 /to 2019-10-15 1600 /force`

```
Got it. I've added this task:
  [E][ ] trip (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)
Now you have 2 tasks in the list.
```

// A force-added clash isn't remembered -- it won't be re-flagged later,
// since the check only runs when a new event is added.

## Feature XYZ

// Feature details