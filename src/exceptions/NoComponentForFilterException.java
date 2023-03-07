package exceptions;

public class NoComponentForFilterException extends Exception {
    private static final String ERROR_MESSAGE = "Could not find any %s for the selected filter(s). Try again!";

    public NoComponentForFilterException(String componentType) {
        super(String.format(ERROR_MESSAGE, componentType));
    }
}
