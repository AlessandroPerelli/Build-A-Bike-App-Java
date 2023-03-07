package exceptions;

public class UserNotFoundException extends Exception {
    private static final String ERROR_MESSAGE = "The user with the provided credentials does not exist!";

    public UserNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
