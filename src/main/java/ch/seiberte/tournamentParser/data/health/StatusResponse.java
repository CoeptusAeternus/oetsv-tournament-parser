package ch.seiberte.tournamentParser.data.health;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Health status payload including overall status and diagnostic message")
public record StatusResponse(Status status, String message) {
}
