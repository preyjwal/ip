/**
 * Thrown when a user command can't be understood or acted on (e.g. a missing
 * "/by" marker, an out-of-range task number). The message is written to be
 * shown to the user as-is, so callers should phrase it as a helpful reply
 * rather than a technical error description.
 */
public class StephException extends Exception {
    public StephException(String message) {
        super(message);
    }
}
