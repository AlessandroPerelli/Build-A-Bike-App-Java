package exceptions;

public class IncompleteBicycleException extends Exception {
    private static final String ERROR_MESSAGE = "Cannot proceed with the order, since " +
            "the created bicycle is incomplete. Please check your selections and try again!";

    public IncompleteBicycleException() {
        super(ERROR_MESSAGE);
    }
}
