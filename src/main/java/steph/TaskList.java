package steph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import steph.task.Task;

/**
 * The list of tasks Steph is tracking, together with the operations the
 * commands need: adding, removing, retrieving by position, and reporting the
 * count.
 *
 * <p>This class holds the tasks and nothing else -- no input/output and no
 * command parsing -- so it can be created and exercised in a test without a
 * console or a save file.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, e.g. the ones just read
     * from the save file. The tasks are copied into a new list, so later
     * changes here do not affect the list that was passed in.
     *
     * @param initialTasks The tasks to start with.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at a 0-based position. The caller is expected to have
     * already validated the index (see Steph's task-number parsing).
     *
     * @param index 0-based position in the list.
     * @return The task at that position.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Removes the task at a 0-based position and returns it.
     *
     * @param index 0-based position in the list.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Returns a read-only view of the tasks, in order, for callers that need
     * the whole list at once (e.g. Storage when writing the save file).
     *
     * @return An unmodifiable list of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Returns the tasks whose name contains {@code keyword}, matched
     * case-insensitively and kept in their current order.
     *
     * <p>The result is a fresh snapshot: it is unmodifiable, and later changes
     * to this list are not reflected in it. An empty list means nothing matched.
     *
     * @param keyword the text to look for within each task's name
     * @return the matching tasks, in list order
     */
    public List<Task> findMatch(String keyword) {
        String loweredKeyword = keyword.toLowerCase();
        return this.tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(loweredKeyword))
                .toList();
    }
}
