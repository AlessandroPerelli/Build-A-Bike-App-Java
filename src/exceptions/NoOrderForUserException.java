package exceptions;

public class NoOrderForUserException extends Exception {
    public static final String ERROR_MESSAGE = "Hello, %s %s. There are no orders assigned to you!";

    public NoOrderForUserException(String forename, String surname) {
        super(String.format(ERROR_MESSAGE, forename, surname));
    }
}
