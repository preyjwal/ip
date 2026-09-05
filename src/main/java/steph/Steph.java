package steph;

import java.io.IOException;
import java.util.List;

import steph.task.Task;

/**
 * Entry point and top-level coordinator. A Steph owns the three collaborators
 * it needs -- a {@link Ui} for talking to the user, a {@link Storage} for the
 * save file, and a {@link TaskList} for the tasks -- and its {@link #run()}
 * method is the read-parse-act loop that ties them together. Parsing lives in
 * {@link Parser}, so this class only orchestrates.
 */
public class Steph {

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Sets up the collaborators and loads any previously saved tasks.
     *
     * @param filePath Where the task list is read from and written to.
     */
    public Steph(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            loaded = new TaskList();
        }
        tasks = loaded;
    }

    /**
     * Runs the command loop until the user types "bye" or the input ends:
     * read a line, ask {@link Parser} what it means, act on it, and save if
     * the task list changed.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (command.equals("bye")) {
                break;
            }

            // Parser turns the line into a Command and its argument text, and
            // throws StephException on anything malformed; the catch below shows
            // that message, so no handler has to report input errors itself.
            try {
                Command commandType = Parser.parseCommand(command);
                String argument = Parser.parseArguments(command);
                switch (commandType) {
                    case LIST -> handleList();
                    case MARK -> handleMark(argument, true);
                    case UNMARK -> handleMark(argument, false);
                    case TODO -> addTask(Parser.parseToDo(argument));
                    case DEADLINE -> addTask(Parser.parseDeadline(argument));
                    case EVENT -> addTask(Parser.parseEvent(argument));
                    case DELETE -> handleDeleteTask(argument);
                    case FIND -> handleFindTask(Parser.parseFind(argument));
                    default -> throw new StephException("Uh oh... I dont understand that");
                }

                // "list" and "find" only read the task list; every other command
                // changes it, so only those need the file rewritten.
                boolean isReadOnlyCommand = commandType == Command.LIST || commandType == Command.FIND;
                if (!isReadOnlyCommand) {
                    storage.save(tasks.asList());
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

    /**
     * Launches Steph, using {@code ./data/steph.txt} as the save file.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Steph("./data/steph.txt").run();
    }

    private void handleList() {
        ui.showMessage(numberedList("Here are the tasks in your list:", tasks.asList()));
    }

    private void handleMark(String argument, boolean markAsDone) throws StephException {
        String commandWord = markAsDone ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(argument, tasks.size(), commandWord);

        Task task = tasks.get(taskIndex);
        if (markAsDone) {
            task.complete();
            ui.showMessage("Awesome! I've marked this task as done:\n  " + task);
        } else {
            task.uncomplete();
            ui.showMessage("OK, I've marked this task as not done yet:\n  " + task);
        }
    }

    private void handleDeleteTask(String argument) throws StephException {
        int taskIndex = Parser.parseTaskIndex(argument, tasks.size(), "delete");
        Task deletedTask = tasks.remove(taskIndex);
        ui.showMessage("Okay! I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private void handleFindTask(String keyword) {
        List<Task> matched = tasks.findMatch(keyword);
        if (matched.isEmpty()) {
            ui.showMessage("I couldn't find any tasks matching \"" + keyword + "\".");
            return;
        }
        ui.showMessage(numberedList("Here are the matching tasks in your list:", matched));
    }

    /**
     * Builds a display block with {@code header} on the first line, then every
     * task on its own line numbered from 1 -- the format both "list" and "find"
     * print.
     *
     * @param header the first line, describing what the list is
     * @param items  the tasks to number, in the order given
     * @return the assembled multi-line string
     */
    private static String numberedList(String header, List<Task> items) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < items.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(items.get(i));
        }
        return message.toString();
    }

    private void addTask(Task newTask) {
        tasks.add(newTask);
        ui.showMessage("Got it. I've added this task:\n  " + newTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
