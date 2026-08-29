package steph.task;

/**
 * A task with only a name -- no date or time attached.
 */
public class ToDo extends Task {

    /**
     * Constructs a to-do with the given name.
     *
     * @param name Description of the task.
     */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toFileFormat() {
        return "T" + super.toFileFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
