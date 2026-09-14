package steph.task;

import java.time.LocalDateTime;
import java.time.LocalTime;

import steph.DateTimes;

/**
 * A task that spans a range, from a start date-time to an end date-time.
 */
public class Event extends Task {

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an event with the given name and start and end date-times.
     *
     * @param name Description of the task.
     * @param from Start date-time of the event.
     * @param to   End date-time of the event.
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns {@code boundary}, or the start of the next day if {@code boundary}
     * is exactly midnight. A midnight end date-time usually comes from a
     * date-only user input (e.g. "2019-10-15" with no time), which is meant to
     * mean "through the end of that day" rather than the single instant the
     * day begins -- this expansion makes that reading explicit for overlap
     * checks (see {@link #clashesWith}).
     *
     * @param boundary The date-time to expand if it falls on midnight.
     * @return The expanded date-time, or {@code boundary} unchanged.
     */
    private static LocalDateTime expandEndIfMidnight(LocalDateTime boundary) {
        return boundary.toLocalTime().equals(LocalTime.MIDNIGHT) ? boundary.plusDays(1) : boundary;
    }

    /**
     * Returns whether this event's span overlaps {@code other}'s. Ranges are
     * half-open ({@code [from, to)}), so two events that are merely
     * back-to-back -- one ending exactly when the other starts -- do not
     * clash. An end date-time that falls exactly on midnight is treated as
     * running through the end of that day rather than as a single instant
     * (see {@link #expandEndIfMidnight}).
     *
     * @param other The event to compare against.
     * @return True if the two events' spans overlap.
     */
    public boolean clashesWith(Event other) {
        LocalDateTime thisEnd = expandEndIfMidnight(this.to);
        LocalDateTime otherEnd = expandEndIfMidnight(other.to);
        return this.from.isBefore(otherEnd) && other.from.isBefore(thisEnd);
    }

    /**
     * Returns the full save-file line for this event, e.g.
     * {@code "E | 0 | camp | 2019-10-15T00:00 | 2019-10-16T00:00"}.
     */
    @Override
    public String toFileFormat() {
        return "E" + super.toFileFormat()
                + " | " + DateTimes.toStorageFormat(this.from)
                + " | " + DateTimes.toStorageFormat(this.to);
    }

    /**
     * Returns this event as
     * {@code "[E][ ] camp (from: Oct 15 2019 to: Oct 16 2019)"}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.toDisplayFormat(this.from)
                + " to: " + DateTimes.toDisplayFormat(this.to) + ")";
    }
}
