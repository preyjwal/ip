import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Steph {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/steph.txt");
        ArrayList<Task> tasks;

        try {
            tasks = storage.load();
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new ArrayList<>();
        }

        ui.showWelcome();

        // Process each command until the user types "bye", or input runs out
        // (e.g. when input is piped from a file instead of typed interactively).
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.equals("bye")) {
                break;
            }

            // Split into the command keyword and its argument, e.g. "mark 2" -> "mark", "2".
            String[] parts = command.split(" ", 2);
            String keyword = parts[0];
            String argument = parts.length > 1 ? parts[1].trim() : "";

            // Handlers throw StephException instead of printing their own error message,
            // so every "can't understand this command" case is reported the same way
            // from one place, instead of each handler repeating ui.showError(...); return;
            try {
                Command commandType = Command.fromKeyword(keyword);
                if (commandType == null) {
                    throw new StephException("Hmm.. I don't understand that command: \"" + keyword + "\".");
                }
                switch (commandType) {
                case LIST -> handleList(ui, tasks);
                case MARK -> handleMark(ui, tasks, argument, true);
                case UNMARK -> handleMark(ui, tasks, argument, false);
                case TODO -> handleAddToDo(ui, tasks, argument);
                case DEADLINE -> handleAddDeadline(ui, tasks, argument);
                case EVENT -> handleAddEvent(ui, tasks, argument);
                case DELETE -> handleDeleteTask(ui, tasks, argument);
                }

                if (commandType != Command.LIST) { // Every other command mutates the tasks list
                    storage.save(tasks);
                }

            } catch (StephException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Sorry, I couldn't save your tasks: " + e.getMessage());
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    private static void handleList(Ui ui, ArrayList<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        ui.showMessage(message.toString());
    }

    private static void handleMark(Ui ui, ArrayList<Task> tasks, String argument, boolean markAsDone)
            throws StephException {
        int taskIndex = parseTaskIndex(argument, tasks.size());
        if (taskIndex == -1) {
            String commandName = markAsDone ? "mark" : "unmark";
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"" + commandName + " <task-number>\" with a valid task number.");
        }

        Task task = tasks.get(taskIndex);
        if (markAsDone) {
            task.complete();
            ui.showMessage("Awesome! I've marked this task as done:\n  " + task);
        } else {
            task.uncomplete();
            ui.showMessage("OK, I've marked this task as not done yet:\n  " + task);
        }
    }

    private static void handleAddToDo(Ui ui, ArrayList<Task> tasks, String argument) throws StephException {
        if (argument.isEmpty()) {
            throw new StephException("Hmm.. I don't understand that.\nPlease type \"todo <task-name>\".");
        }
        addTask(ui, tasks, new ToDo(argument));
    }

    /**
     * Parses {@code <task-name> /by <yyyy-mm-dd> [HHmm]} and adds a Deadline
     * task. The "/by" marker is used as the split point since a task name isn't
     * expected to contain it.
     */
    private static void handleAddDeadline(Ui ui, ArrayList<Task> tasks, String argument) throws StephException {
        int byIndex = argument.indexOf("/by");
        if (byIndex == -1) {
            throw new StephException(
                    "Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <yyyy-mm-dd>\".");
        }

        String name = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + "/by".length()).trim();
        if (name.isEmpty() || by.isEmpty()) {
            throw new StephException(
                    "Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <yyyy-mm-dd>\".");
        }
        addTask(ui, tasks, new Deadline(name, parseDateTime(by)));
    }

    /**
     * Parses {@code <task-name> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd>
     * [HHmm]} and adds an Event task, splitting first on "/from" and then on
     * "/to" within the remainder.
     */
    private static void handleAddEvent(Ui ui, ArrayList<Task> tasks, String argument) throws StephException {
        int fromIndex = argument.indexOf("/from");
        int toIndex = argument.indexOf("/to");
        boolean validOrder = fromIndex != -1 && toIndex != -1 && fromIndex < toIndex;

        if (!validOrder) {
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\".");
        }

        String name = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = argument.substring(toIndex + "/to".length()).trim();

        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\".");
        }
        addTask(ui, tasks, new Event(name, parseDateTime(from), parseDateTime(to)));
    }

    /**
     * Parses a user-supplied date, with an optional 24-hour time
     * ("yyyy-mm-dd" or "yyyy-mm-dd HHmm", e.g. 2019-10-15 1800). A date with no
     * time is taken as midnight. Throws a StephException with a readable hint if
     * the text isn't valid, so the mistake is reported like any other bad
     * command instead of crashing.
     */
    private static LocalDateTime parseDateTime(String text) throws StephException {
        try {
            return DateTimes.parseUserInput(text);
        } catch (DateTimeParseException e) {
            throw new StephException("Hmm.. I couldn't read \"" + text + "\" as a date.\n"
                    + "Please use the format yyyy-mm-dd or yyyy-mm-dd HHmm, e.g. 2019-10-15 1800.");
        }
    }

    private static void handleDeleteTask(Ui ui, ArrayList<Task> tasks, String argument) throws StephException {
        int taskIndex = parseTaskIndex(argument, tasks.size());
        if (taskIndex == -1) {
            throw new StephException("Hmm.. I don't understand that.\n"
                    + "Please type \"delete <task-number>\" with a valid task number.");
        }
        Task deletedTask = tasks.get(taskIndex);
        tasks.remove(taskIndex);
        ui.showMessage("Okay! I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void addTask(Ui ui, ArrayList<Task> tasks, Task newTask) {
        tasks.add(newTask);
        ui.showMessage("Got it. I've added this task:\n  " + newTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Parses a 1-based task number typed by the user and validates it against the
     * current task list. Returns the corresponding 0-based index, or -1 if the
     * argument is missing, not a number, or out of range.
     */
    private static int parseTaskIndex(String argument, int taskCount) {
        if (argument.isEmpty()) {
            return -1;
        }
        try {
            int index = Integer.parseInt(argument) - 1;
            return (index >= 0 && index < taskCount) ? index : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
