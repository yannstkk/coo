package exceptions;

/**
 * Exception thrown when a vehicle cannot be parked at a station.
 */
public class CannotParkException extends Exception {
    /**
     * @param message the exception message
     */
    public CannotParkException(String message) {
        super(message);
    }
}