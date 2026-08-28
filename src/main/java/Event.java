import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task that spans a date range, from a start date to an end date.
 */
public class Event extends Task {

    /** Format used when showing the dates to the user, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate from;
    protected LocalDate to;

    public Event(String name, LocalDate from, LocalDate to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        // LocalDate.toString() is ISO-8601 ("2019-10-15"), read back by LocalDate.parse.
        return "E" + super.toFileFormat() + " | " + this.from + " | " + this.to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from.format(DISPLAY_FORMAT)
                + " to: " + this.to.format(DISPLAY_FORMAT) + ")";
    }
}
