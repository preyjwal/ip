import java.util.Scanner;
import java.util.ArrayList;

public class Steph {
    private static final String NAME = "Steph";
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> tasks = new ArrayList<>();

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

        // Add each command as a task to tasks until the user types "bye", or input runs out
        // (e.g. when input is piped from a file instead of typed interactively).
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.equals("bye")) {
                break;
            } else if (command.equals("list")) {
                System.out.println(LINE);
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
                System.out.println(LINE);
            } else {
                tasks.add(command);
                System.out.println(LINE);
                System.out.println("added: " + command);
                System.out.println(LINE);
            }
        }

        System.out.println(LINE);
        System.out.println("Goodbye. Hope to see you again soon!");
        System.out.println(LINE);

        scanner.close();
    }
}
