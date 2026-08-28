import java.time.LocalDateTime;

/**
 * A task that spans a range, from a start date-time to an end date-time.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return "E" + super.toFileFormat()
                + " | " + DateTimes.toStorageFormat(this.from)
                + " | " + DateTimes.toStorageFormat(this.to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.toDisplayFormat(this.from)
                + " to: " + DateTimes.toDisplayFormat(this.to) + ")";
    }
}
