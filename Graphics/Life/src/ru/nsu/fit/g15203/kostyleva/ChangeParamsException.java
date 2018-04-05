package ru.nsu.fit.g15203.kostyleva;

public class ChangeParamsException extends Throwable{
    private final String message;

    ChangeParamsException(String msg) {
            this.message = msg;
    }
        @Override
        public String toString() {
            return this.message;
    }
}
