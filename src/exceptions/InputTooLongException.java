package exceptions;

public class InputTooLongException extends Exception {
    private static final String ERROR_MESSAGE = "Provided input is too long. Input cannot be longer than %s characters!";

    public InputTooLongException(int maxLength) {
        super(String.format(ERROR_MESSAGE, maxLength));
    }
}
