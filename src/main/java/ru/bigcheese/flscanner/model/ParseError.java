package ru.bigcheese.flscanner.model;

public class ParseError {

    private final String message;
    private final String stacktrace;

    public ParseError(String message, String stacktrace) {
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public String getMessage() {
        return message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    public String toString() {
        return "ParseError{" +
                "message='" + message + '\'' +
                '}';
    }
}
