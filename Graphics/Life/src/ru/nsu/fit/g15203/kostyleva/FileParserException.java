package ru.nsu.fit.g15203.kostyleva;

public class FileParserException extends Throwable {
    private final String message;

    FileParserException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}