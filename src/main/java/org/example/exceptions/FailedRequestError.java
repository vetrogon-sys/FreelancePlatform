package org.example.exceptions;

public class FailedRequestError extends Exception {

    public FailedRequestError(String message) {
        super(message);
    }
}
