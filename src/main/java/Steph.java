import java.io.IOException;

public class Steph {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/steph.txt");
        TaskList tasks;

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        ui.showWelcome();

        // Process each command until the user types "bye", or input runs out
        // (e.g. when input is piped from a file instead of typed interactively).
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
                case LIST -> handleList(ui, tasks);
                case MARK -> handleMark(ui, tasks, argument, true);
                case UNMARK -> handleMark(ui, tasks, argument, false);
                case TODO -> addTask(ui, tasks, Parser.parseToDo(argument));
                case DEADLINE -> addTask(ui, tasks, Parser.parseDeadline(argument));
                case EVENT -> addTask(ui, tasks, Parser.parseEvent(argument));
                case DELETE -> handleDeleteTask(ui, tasks, argument);
                }

                if (commandType != Command.LIST) { // Every other command mutates the tasks list
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

    private static void handleList(Ui ui, TaskList tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        ui.showMessage(message.toString());
    }

    private static void handleMark(Ui ui, TaskList tasks, String argument, boolean markAsDone)
            throws StephException {
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

    private static void handleDeleteTask(Ui ui, TaskList tasks, String argument) throws StephException {
        int taskIndex = Parser.parseTaskIndex(argument, tasks.size(), "delete");
        Task deletedTask = tasks.remove(taskIndex);
        ui.showMessage("Okay! I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void addTask(Ui ui, TaskList tasks, Task newTask) {
        tasks.add(newTask);
        ui.showMessage("Got it. I've added this task:\n  " + newTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
