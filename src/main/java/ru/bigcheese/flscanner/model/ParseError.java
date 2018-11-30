package ru.bigcheese.flscanner.model;

public class ParseError {

    private final String message;
    private final String exception;
    private final String stacktrace;

    public ParseError(String message, String exception, String stacktrace) {
        this.message = message;
        this.exception = exception;
        this.stacktrace = stacktrace;
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    public String toString() {
        return "ParseError{" +
                "message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }
}
