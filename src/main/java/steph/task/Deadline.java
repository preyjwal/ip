package steph.task;

import java.time.LocalDateTime;

import steph.DateTimes;

/**
 * A task that must be done by a certain date, optionally with a time of day.
 */
public class Deadline extends Task {

    private final LocalDateTime by;

    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D" + super.toFileFormat() + " | " + DateTimes.toStorageFormat(this.by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.toDisplayFormat(this.by) + ")";
    }
}
