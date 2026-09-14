package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link Steph#getResponse(String)} and the small pieces of
 * state the GUI reads alongside it ({@link Steph#getCommandType()} and
 * {@link Steph#isExit()}).
 *
 * <p>{@code getResponse} is the single entry point both front ends funnel
 * through: it parses a line, mutates the task list, saves, and returns the
 * reply text. The branching that matters here is which command ran (so the
 * GUI can tint the reply), whether a failed command clears that, whether
 * "bye" flips the exit flag, and whether a mutating command is actually
 * persisted. Each test gets its own {@link TempDir} save file so nothing
 * touches the real {@code ./data/steph.txt}.
 */
public class StephTest {

    @TempDir
    private Path tempDir;

    private Steph newSteph() {
        return new Steph(tempDir.resolve("steph.txt").toString());
    }

    @Test
    public void getResponse_todoCommand_addsTaskAndTracksCommandType() {
        Steph steph = newSteph();

        String response = steph.getResponse("todo buy milk");

        assertEquals("Got it. I've added this task:\n  [T][ ] buy milk\n"
                + "Now you have 1 tasks in the list.", response);
        assertEquals("TODO", steph.getCommandType());
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorAndClearsCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo buy milk"); // leaves commandType == "TODO"

        String response = steph.getResponse("sing a song");

        assertEquals("Hmm.. I don't understand that command: \"sing\".", response);
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void getResponse_markCommand_marksTaskAndTracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo borrow book");

        String response = steph.getResponse("mark 1");

        assertEquals("Awesome! I've marked this task as done:\n  [T][X] borrow book", response);
        assertEquals("MARK", steph.getCommandType());
    }

    @Test
    public void getResponse_deleteCommand_removesTaskAndTracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo one");
        steph.getResponse("todo two");

        String response = steph.getResponse("delete 1");

        assertEquals("Okay! I've removed this task:\n  [T][ ] one\n"
                + "Now you have 1 tasks in the list.", response);
        assertEquals("DELETE", steph.getCommandType());
    }

    @Test
    public void getResponse_listCommand_tracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo read book");

        String response = steph.getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", response);
        assertEquals("LIST", steph.getCommandType());
    }

    @Test
    public void getResponse_bye_setsExitFlagAndReturnsFarewell() {
        Steph steph = newSteph();
        assertFalse(steph.isExit());

        String response = steph.getResponse("bye");

        assertEquals("Goodbye. Hope to see you again soon!", response);
        assertTrue(steph.isExit());
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void getResponse_mutatingCommand_isPersistedForTheNextSession() {
        newSteph().getResponse("todo persist me");

        // A fresh Steph on the same save file should load what the first one wrote.
        String response = newSteph().getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] persist me", response);
    }

    @Test
    public void getResponse_eventClashesWithExistingPendingEvent_repliesWithClashMessage() {
        Steph steph = newSteph();
        steph.getResponse("event trip /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15");

        assertEquals("This clashes with an existing event:\n"
                + "  [E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)\n"
                + "Add '/force' to the command if you want to schedule it anyway.", response);
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void getResponse_blockedEventAdd_notPersistedAndTaskCountUnchanged() {
        newSteph().getResponse("event trip /from 2019-10-15 /to 2019-10-15");
        Steph steph = newSteph();
        steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("list");

        assertEquals("Here are the tasks in your list:\n"
                + "1.[E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)", response);
    }

    @Test
    public void getResponse_eventWithTrailingForceFlag_bypassesClashCheckAndAdds() {
        Steph steph = newSteph();
        steph.getResponse("event trip /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15 /force");

        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] conflict (from: Oct 15 2019 to: Oct 15 2019)\n"
                + "Now you have 2 tasks in the list.", response);
        assertEquals("EVENT", steph.getCommandType());
    }
}
