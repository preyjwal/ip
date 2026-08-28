import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Turns a raw line of user input into values the command loop can act on: the
 * {@link Command} it names, its argument text, and -- for the commands that
 * need it -- the task number or the fully-built task those arguments describe.
 *
 * <p>Every method here is pure text-to-value work. The parser never touches
 * the task list, the UI, or the save file; on malformed input it throws a
 * {@link StephException} whose message is ready to show the user.
 */
public class Parser {

    /**
     * Identifies which command a line names, from its first word.
     *
     * @param fullCommand the whole line as typed
     * @return the matching Command
     * @throws StephException if the first word is not a known command keyword
     */
    public static Command parseCommand(String fullCommand) throws StephException {
        String keyword = fullCommand.split(" ", 2)[0];
        Command command = Command.fromKeyword(keyword);
        if (command == null) {
            throw new StephException("Hmm.. I don't understand that command: \"" + keyword + "\".");
        }
        return command;
    }

    /**
     * Returns the part of the line after the command keyword, trimmed. Empty
     * when the line is only the keyword.
     *
     * @param fullCommand the whole line as typed
     * @return the argument text
     */
    public static String parseArguments(String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses the task number for a mark / unmark / delete command and converts
     * it to a 0-based index into a list of {@code taskCount} tasks.
     *
     * @param arguments   the text after the command keyword
     * @param taskCount   how many tasks currently exist, for the range check
     * @param commandWord the keyword to quote back in the error message
     * @return the 0-based index the number refers to
     * @throws StephException if the text is missing, not a number, or out of range
     */
    public static int parseTaskIndex(String arguments, int taskCount, String commandWord) throws StephException {
        try {
            int index = Integer.parseInt(arguments) - 1;
            if (index >= 0 && index < taskCount) {
                return index;
            }
        } catch (NumberFormatException e) {
            // Not a number, fall through to the shared "valid task number" error.
        }
        throw new StephException("Hmm.. I don't understand that.\n"
                + "Please type \"" + commandWord + " <task-number>\" with a valid task number.");
    }

    /**
     * Builds a ToDo from its arguments.
     *
     * @param arguments the task name
     * @return the new ToDo
     * @throws StephException if the name is empty
     */
    public static ToDo parseToDo(String arguments) throws StephException {
        if (arguments.isEmpty()) {
            throw new StephException("Hmm.. I don't understand that.\nPlease type \"todo <task-name>\".");
        }
        return new ToDo(arguments);
    }

    /**
     * Builds a Deadline from {@code <task-name> /by <yyyy-mm-dd> [HHmm]}. The
     * "/by" marker is used as the split point since a task name isn't expected
     * to contain it.
     *
     * @param arguments the text after the "deadline" keyword
     * @return the new Deadline
     * @throws StephException if "/by" is missing, either side is empty, or the
     *                        date cannot be read
     */
    public static Deadline parseDeadline(String arguments) throws StephException {
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new StephException(
                    "Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <yyyy-mm-dd>\".");
        }

        String name = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (name.isEmpty() || by.isEmpty()) {
            throw new StephException(
                    "Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <yyyy-mm-dd>\".");
        }
        return new Deadline(name, parseDateTime(by));
    }

    /**
     * Builds an Event from
     * {@code <task-name> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd> [HHmm]},
     * splitting first on "/from" and then on "/to" within the remainder.
     *
     * @param arguments the text after the "event" keyword
     * @return the new Event
     * @throws StephException if the markers are missing or out of order, any
     *                        part is empty, or a date cannot be read
     */
    public static Event parseEvent(String arguments) throws StephException {
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        boolean validOrder = fromIndex != -1 && toIndex != -1 && fromIndex < toIndex;

        if (!validOrder) {
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\".");
        }

        String name = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();

        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\".");
        }
        return new Event(name, parseDateTime(from), parseDateTime(to));
    }

    /**
     * Parses a user-supplied date, optionally with a 24-hour time ("yyyy-mm-dd"
     * or "yyyy-mm-dd HHmm", e.g. 2019-10-15 1800). A date with no time is taken
     * as midnight.
     *
     * @param text the date (and optional time) as typed
     * @return the parsed date-time
     * @throws StephException with a readable hint if the text is not a valid date
     */
    private static LocalDateTime parseDateTime(String text) throws StephException {
        try {
            return DateTimes.parseUserInput(text);
        } catch (DateTimeParseException e) {
            throw new StephException("Hmm.. I couldn't read \"" + text + "\" as a date.\n"
                    + "Please use the format yyyy-mm-dd or yyyy-mm-dd HHmm, e.g. 2019-10-15 1800.");
        }
    }
}
