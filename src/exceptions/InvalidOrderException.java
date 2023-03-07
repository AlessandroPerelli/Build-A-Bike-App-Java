package exceptions;

public class InvalidOrderException extends Exception {
    private static final String ERROR_MESSAGE = "The order with serial number %s has been corrupted. " +
                                                "Please contact a staff member.";

    public InvalidOrderException(String orderSerialNumber) {
        super(String.format(ERROR_MESSAGE, orderSerialNumber));
    }
}
