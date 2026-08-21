public class Task {
    private String name;
    private boolean isDone;

    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    public void complete() {
        this.isDone = true;
    }

    public void uncomplete() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.name;
        }
        return "[ ] " + this.name;
    }
}
