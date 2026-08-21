import java.util.ArrayList;
import java.util.Scanner;

public class Steph {
    private static final String NAME = "Steph";
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        String banner = " ____  _             _     \n"
                + "/ ___|| |_ ___ _ __ | |__  \n"
                + "\\___ \\| __/ _ \\ '_ \\| '_ \\ \n"
                + " ___) | ||  __/ |_) | | | |\n"
                + "|____/ \\__\\___| .__/|_| |_|\n"
                + "              |_|";

        System.out.println(LINE);
        System.out.println(banner);
        System.out.printf("Hello! I'm %s.%nGlad to see you!%nWhat can I help you with?%n", NAME);
        System.out.println(LINE);

        // Process each command until the user types "bye", or input runs out
        // (e.g. when input is piped from a file instead of typed interactively).
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.equals("bye")) {
                break;
            }

            // Split into the command keyword and its argument, e.g. "mark 2" -> "mark", "2".
            String[] parts = command.split(" ", 2);
            String keyword = parts[0];
            String argument = parts.length > 1 ? parts[1].trim() : "";

            switch (keyword) {
            case "list" -> handleList(tasks);
            case "mark" -> handleMark(tasks, argument, true);
            case "unmark" -> handleMark(tasks, argument, false);
            case "todo" -> handleAddToDo(tasks, argument);
            case "deadline" -> handleAddDeadline(tasks, argument);
            case "event" -> handleAddEvent(tasks, argument);
            default -> printWrapped("Hmm.. I don't understand that command: \"" + keyword + "\".");
            }
        }

        System.out.println(LINE);
        System.out.println("Goodbye. Hope to see you again soon!");
        System.out.println(LINE);

        scanner.close();
    }

    private static void handleList(ArrayList<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        printWrapped(message.toString());
    }

    private static void handleMark(ArrayList<Task> tasks, String argument, boolean markAsDone) {
        int taskIndex = parseTaskIndex(argument, tasks.size());
        if (taskIndex == -1) {
            String commandName = markAsDone ? "mark" : "unmark";
            printWrapped("Hmm.. I don't understand that.\n"
                    + "Please type \"" + commandName + " <task-number>\" with a valid task number.");
            return;
        }

        Task task = tasks.get(taskIndex);
        if (markAsDone) {
            task.complete();
            printWrapped("Awesome! I've marked this task as done:\n  " + task);
        } else {
            task.uncomplete();
            printWrapped("OK, I've marked this task as not done yet:\n  " + task);
        }
    }

    private static void handleAddToDo(ArrayList<Task> tasks, String argument) {
        if (argument.isEmpty()) {
            printWrapped("Hmm.. I don't understand that.\nPlease type \"todo <task-name>\".");
            return;
        }
        addTask(tasks, new ToDo(argument));
    }

    /**
     * Parses "<task-name> /by <when>" and adds a Deadline task. The "/by" marker
     * is used as the split point since a task name isn't expected to contain it.
     */
    private static void handleAddDeadline(ArrayList<Task> tasks, String argument) {
        int byIndex = argument.indexOf("/by");
        if (byIndex == -1) {
            printWrapped("Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <date/time>\".");
            return;
        }

        String name = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + "/by".length()).trim();
        if (name.isEmpty() || by.isEmpty()) {
            printWrapped("Hmm.. I don't understand that.\nPlease type \"deadline <task-name> /by <date/time>\".");
            return;
        }
        addTask(tasks, new Deadline(name, by));
    }

    /**
     * Parses "<task-name> /from <start> /to <end>" and adds an Event task, splitting
     * first on "/from" and then on "/to" within the remainder.
     */
    private static void handleAddEvent(ArrayList<Task> tasks, String argument) {
        int fromIndex = argument.indexOf("/from");
        int toIndex = argument.indexOf("/to");
        boolean validOrder = fromIndex != -1 && toIndex != -1 && fromIndex < toIndex;

        if (!validOrder) {
            printWrapped("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <start> /to <end>\".");
            return;
        }

        String name = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = argument.substring(toIndex + "/to".length()).trim();

        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printWrapped("Hmm.. I don't understand that.\n"
                    + "Please type \"event <task-name> /from <start> /to <end>\".");
            return;
        }
        addTask(tasks, new Event(name, from, to));
    }

    private static void addTask(ArrayList<Task> tasks, Task newTask) {
        tasks.add(newTask);
        printWrapped("Got it. I've added this task:\n  " + newTask
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

    private static void printWrapped(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
