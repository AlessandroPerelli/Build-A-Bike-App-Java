package exceptions;

public class InvalidAddressException extends Exception {
    public static final String ERROR_MESSAGE = "The provided provided house number and postcode " +
            "do not match the rest of the address, or otherwise.";

    public InvalidAddressException() {
        super(ERROR_MESSAGE);
    }
}
