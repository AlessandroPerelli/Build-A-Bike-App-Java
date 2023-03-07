package exceptions;

public class BicycleNotFoundException extends Exception {
    private static final String ERROR_MESSAGE = "The bicycle with serial number %s could not be found";

    public BicycleNotFoundException(String serialNumber) {
        super(String.format(ERROR_MESSAGE, serialNumber));
    }
}
