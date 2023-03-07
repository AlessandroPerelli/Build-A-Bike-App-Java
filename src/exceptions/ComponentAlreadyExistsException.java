package exceptions;

public class ComponentAlreadyExistsException extends Exception {
    private static final String ERROR_MESSAGE = "The product cannot be created, since it already exists!";

    public ComponentAlreadyExistsException() {
        super(ERROR_MESSAGE);
    }
}
