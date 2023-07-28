package ch.seiberte.tournamentParser.exceptions;

public class UnableToReadDataExcpetion extends RuntimeException{

    public UnableToReadDataExcpetion() {
    }

    public UnableToReadDataExcpetion(String message) {
        super(message);
    }
}
