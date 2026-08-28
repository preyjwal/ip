import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Storage {
    private final Path path;

    public Storage(String filePath) {
        this.path = Paths.get(filePath);
    }

    public ArrayList<Task> load() throws IOException {
        if (!Files.exists(this.path)) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();

        for (String line : Files.readAllLines(this.path)) {
            try {
                tasks.add(parseTask(line));
            } catch (StephException e) {
                System.out.println("Skipping corrupted line: " + line);
            }
        }
        return tasks;
    }

    /**
     * Writes list of tasks to the file at the filepath.
     * If there is no file at that filepath, it creates a new file and writes to it.
     * Called after every change and overwrites the whole file.
     *
     * @param tasks List of tasks.
     * @throws IOException If an I/O error occurs
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(this.path.getParent());
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileFormat());
        }
        Files.write(this.path, lines);
    }

    /**
     * Parses one save-file line into the matching Task subclass.
     * Assumes task descriptions contain no '|' characters.
     *
     * @param line A line in the format "T | 1 | name" (with extra fields for D and E).
     * @return The reconstructed task, marked done when the status flag is "1".
     * @throws StephException If the line has an unknown type or is missing fields.
     */
    private Task parseTask(String line) throws StephException {
        String[] parts = line.split("\\|");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        try {
            Task task = switch (parts[0]) {
                case "T" -> new ToDo(parts[2]);
                case "D" -> new Deadline(parts[2], parts[3]);
                case "E" -> new Event(parts[2], parts[3], parts[4]);
                default -> throw new StephException("Unknown task type in line: " + line);
            };

            if (parts[1].equals("1")) {
                task.complete();
            }

            return task;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new StephException("Line is missing fields: " + line);
        }
    }
}
