import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task that must be done by a certain date.
 */
public class Deadline extends Task {

    /** Format used when showing the date to the user, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate by;

    public Deadline(String name, LocalDate by) {
        super(name);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        // LocalDate.toString() is ISO-8601 ("2019-10-15"), which LocalDate.parse
        // reads back without a formatter -- so the save file stays round-trippable.
        return "D" + super.toFileFormat() + " | " + this.by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }
}
