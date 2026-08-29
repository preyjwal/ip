package steph.task;

/**
 * A task with just a name and a done/not-done status -- no date attached.
 */
public class ToDo extends Task {

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
