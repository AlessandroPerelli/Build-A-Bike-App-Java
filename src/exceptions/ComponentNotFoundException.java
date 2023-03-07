package exceptions;

public class ComponentNotFoundException extends Exception {
    private static final String ERROR_MESSAGE = "The component with brand %s and serial number %s could not be found!";

    public ComponentNotFoundException(String brandName, String serialNumber) {
        super(String.format(ERROR_MESSAGE, brandName, serialNumber));
    }
}
