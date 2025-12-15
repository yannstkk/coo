package exceptions;

/**
 * Exception thrown when an operation is attempted in an invalid state.
 */
public class IllegalStateException extends Exception {
    /**
     * @param message the exception message
     */
    public IllegalStateException(String message) {
        super(message);
    }
}
