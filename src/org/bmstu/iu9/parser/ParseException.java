package org.bmstu.iu9.parser;

public class ParseException extends Exception {
    private final String message;

    public ParseException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
