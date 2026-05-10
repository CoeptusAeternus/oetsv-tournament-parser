package ch.seiberte.tournamentParser.data.health;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response returned when an operation fails")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private final int status;

    @Schema(description = "Short human-readable error message", example = "Tournament not found")
    private final String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
