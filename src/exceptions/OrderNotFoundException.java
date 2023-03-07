package exceptions;

public class OrderNotFoundException extends Exception {
    private static final String ERROR_MESSAGE = "The order with the number %s could not be found!";

    public OrderNotFoundException(String orderNumber) {
        super(String.format(ERROR_MESSAGE, orderNumber));
    }
}
