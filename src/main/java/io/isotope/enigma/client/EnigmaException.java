package io.isotope.enigma.client;

public class EnigmaException extends RuntimeException {

    public EnigmaException() {
    }

    public EnigmaException(String message) {
        super(message);
    }

    public EnigmaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnigmaException(Throwable cause) {
        super(cause);
    }
}
