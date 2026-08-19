import java.util.Scanner;

public class Steph {
    private static final String NAME = "Steph";
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

        // Echo each command until the user types "bye", or input runs out
        // (e.g. when input is piped from a file instead of typed interactively).
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.equals("bye")) {
                break;
            }
            System.out.println(LINE);
            System.out.println(command);
            System.out.println(LINE);
        }

        System.out.println(LINE);
        System.out.println("Goodbye. Hope to see you again soon!");
        System.out.println(LINE);

        scanner.close();
    }
}
