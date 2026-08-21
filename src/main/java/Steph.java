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
            default -> handleAdd(tasks, command);
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

    private static void handleAdd(ArrayList<Task> tasks, String command) {
        Task newTask = new Task(command);
        tasks.add(newTask);
        printWrapped("added: " + newTask);
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
