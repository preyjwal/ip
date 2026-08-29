package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import steph.task.Task;
import steph.task.ToDo;

/**
 * Unit tests for {@link TaskList}.
 *
 * <p>Most of {@code TaskList} is a thin pass-through to an {@code ArrayList},
 * but two behaviours are deliberate design decisions worth pinning down:
 * <ul>
 *   <li>the {@code List}-taking constructor makes a <em>defensive copy</em>, so
 *       the caller's list and the {@code TaskList} cannot disturb each other;</li>
 *   <li>{@link TaskList#asList()} hands back an <em>unmodifiable</em> view.</li>
 * </ul>
 */
public class TaskListTest {

    @Test
    public void constructor_noArgs_startsEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_fromList_containsTheSameTasksInOrder() {
        Task a = new ToDo("a");
        Task b = new ToDo("b");

        TaskList list = new TaskList(List.of(a, b));

        assertEquals(2, list.size());
        assertSame(a, list.get(0));
        assertSame(b, list.get(1));
    }

    @Test
    public void constructor_fromList_laterChangeToSourceListDoesNotAffectTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));

        TaskList list = new TaskList(source);
        source.add(new ToDo("added after construction"));

        assertEquals(1, list.size());
    }

    @Test
    public void constructor_fromList_changeToTaskListDoesNotAffectSourceList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));

        TaskList list = new TaskList(source);
        list.add(new ToDo("b"));

        assertEquals(1, source.size());
    }

    @Test
    public void addThenGet_returnsThatTask() {
        TaskList list = new TaskList();
        Task task = new ToDo("write tests");

        list.add(task);

        assertSame(task, list.get(0));
    }

    @Test
    public void remove_returnsRemovedTaskAndShrinksList() {
        Task a = new ToDo("a");
        Task b = new ToDo("b");
        TaskList list = new TaskList(List.of(a, b));

        Task removed = list.remove(0);

        assertSame(a, removed);
        assertEquals(1, list.size());
        assertSame(b, list.get(0));
    }

    @Test
    public void asList_isUnmodifiable_mutationThrows() {
        TaskList list = new TaskList(List.of(new ToDo("a")));

        List<Task> view = list.asList();

        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("b")));
    }

    @Test
    public void asList_isALiveView_reflectsLaterAdditions() {
        TaskList list = new TaskList();
        List<Task> view = list.asList();

        list.add(new ToDo("added after asList() was called"));

        assertEquals(1, view.size());
    }
}
