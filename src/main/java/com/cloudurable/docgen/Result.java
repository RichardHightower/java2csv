package com.cloudurable.docgen;

public class Result {
    private final int result;
    private final String output;
    private final String errors;

    private final Exception exception;

    private final boolean complete;

    public Result(int result, String output, String errors, Exception exception, boolean complete) {
        this.result = result;
        this.output = output;
        this.errors = errors;
        this.exception = exception;
        this.complete = complete;
    }

    public int getResult() {
        return result;
    }

    public String getOutput() {
        return output;
    }

    public String getErrors() {
        return errors;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isComplete() {
        return complete;
    }
}
