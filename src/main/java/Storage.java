import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
     * @param tasks the tasks to write, in order
     * @throws IOException If an I/O error occurs
     */
    public void save(List<Task> tasks) throws IOException {
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
     * @param line A line in the format "T | 1 | name" (D and E add ISO
     *             date-times: "D | 1 | name | 2019-10-15T18:00").
     * @return The reconstructed task, marked done when the status flag is "1".
     * @throws StephException If the line has an unknown type, is missing fields,
     *                        or holds an unparseable date.
     */
    private Task parseTask(String line) throws StephException {
        String[] parts = line.split("\\|");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        try {
            Task task = switch (parts[0]) {
                case "T" -> new ToDo(parts[2]);
                case "D" -> new Deadline(parts[2], DateTimes.parseStored(parts[3]));
                case "E" -> new Event(parts[2], DateTimes.parseStored(parts[3]), DateTimes.parseStored(parts[4]));
                default -> throw new StephException("Unknown task type in line: " + line);
            };

            if (parts[1].equals("1")) {
                task.complete();
            }

            return task;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new StephException("Line is malformed: " + line);
        }
    }
}
