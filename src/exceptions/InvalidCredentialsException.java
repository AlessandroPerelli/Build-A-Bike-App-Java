package exceptions;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String error_message) {
        super(error_message);
    }
}
