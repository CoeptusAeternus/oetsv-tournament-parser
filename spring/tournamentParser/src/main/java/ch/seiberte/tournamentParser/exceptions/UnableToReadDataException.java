package ch.seiberte.tournamentParser.exceptions;

public class UnableToReadDataException extends RuntimeException{

    public UnableToReadDataException() {
    }

    public UnableToReadDataException(String message) {
        super(message);
    }
}
