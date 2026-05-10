package ch.seiberte.tournamentParser;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.data.health.ErrorResponse;
import ch.seiberte.tournamentParser.data.health.Status;
import ch.seiberte.tournamentParser.data.health.StatusResponse;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.IAmATeapotException;
import ch.seiberte.tournamentParser.proxys.IKalenderProxy;
import ch.seiberte.tournamentParser.proxys.ListProxy;
import ch.seiberte.tournamentParser.proxys.RenamingTournamentProxy;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@EnableScheduling
@RestController
@OpenAPIDefinition(info = @Info(title = "Tournament Parser API", version = "1.0", description = "API to list and read tournament information"), servers = {
        @Server(url = "http://localhost:12001", description = "Primary HTTP server") })
public class Endpoints {

    private final ITournamentReader tr;
    private final IKalenderProxy kr;

    public Endpoints() {
        this.tr = new RenamingTournamentProxy();
        this.kr = new ListProxy();
    }

    private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return current health of API", description = "Gives a short overview of the status and health of the API and its parsers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Health status and message", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusResponse.class), examples = @ExampleObject(value = "{\"status\":\"OK\",\"message\":\"All parsers responding\"}")))
    })
    public StatusResponse getHealth() {
        Status status = Status.OK;
        String message = "";

        IKalenderReader calendarReader = new OetsvCalendarDataParser();
        ITournamentReader tournamentReader = new OetsvTournamentDataParser();

        try {
            calendarReader.getTournaments();
        } catch (Exception e) {
            status = Status.DEGRADED;
            message = message + e.getMessage() + "\n";
        }

        try {
            tournamentReader.readTournament(1500L);
        } catch (Exception e) {

            status = switch (status) {
                case OK -> Status.DEGRADED;
                case DEGRADED -> Status.ERROR;
                default -> status;
            };

            message = message + e.getMessage() + "\n";
        }

        return new StatusResponse(status, message);
    }

    @CrossOrigin
    @GetMapping(value = "/tournaments", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all available tournaments", description = "Returns a list of all available tournaments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of tournaments found", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ShortTournament.class)), examples = @ExampleObject(value = "[{\"id\":1500,\"name\":\"City Cup\",\"start\":\"2026-06-12T00:00:00\"}]")))
    })
    public List<ShortTournament> returnList() {
        logger.info("request at /tournaments");
        return kr.getTournaments();
    }

    @CrossOrigin
    @GetMapping(value = "/tournaments/{tournamentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a specific tournament", description = "Returns a specific tournament by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LongTournament.class), examples = @ExampleObject(value = "{\"id\":1500,\"name\":\"Mehrflächenturnier\",\"address\":\"Sportzentrum Nö , 3100 St.Pölten, Dr. Adolf Schärf-Straße 25\",\"start\":\"2022-11-12T10:00:00\",\"fee\":\"20,-\",\"classes\":[\"Sch. Sta D\",\"Sch. Sta C\",\"Sch. La D\",\"Sch. La C\",\"Jun. I Sta D\",\"Jun. I Sta C\",\"Jun. I Sta B\",\"Jun. I La D\",\"Jun. I La C\",\"Jun. I La B\",\"Jun. II Sta D\",\"Jun. II Sta C\",\"Jun. II Sta B\",\"Jun. II La D\",\"Jun. II La C\",\"Jun. II La B\",\"Jug. Sta D\",\"Jug. Sta C\",\"Jug. Sta B\",\"Jug. Sta A\",\"Jug. La D\",\"Jug. La C\",\"Jug. La B\",\"Jug. La A\",\"Allg.Kl. Sta D\",\"Allg.Kl. Sta C\",\"Allg.Kl. Sta B\",\"Allg.Kl. Sta A\",\"Allg.Kl. La D\",\"Allg.Kl. La C\",\"Allg.Kl. La B\",\"Allg.Kl. La A\",\"Sen. I Sta D\",\"Sen. I Sta C\",\"Sen. I La D\",\"Sen. I La C\",\"Sen. I La B\",\"Sen. I La S\",\"Sen. II Sta D\",\"Sen. II Sta C\",\"Sen. II Sta B\",\"Sen. II Sta A\",\"Sen. II Sta S\",\"Sen. II La D\",\"Sen. II La C\",\"Sen. II La B\",\"Sen. II La S\",\"Sen. III Sta D\",\"Sen. III Sta C\",\"Sen. III Sta B\",\"Sen. III Sta A\",\"Sen. III Sta S\",\"Sen. III La D\",\"Sen. III La C\",\"Sen. III La B\",\"Sen. III La S\",\"Sch. Sta BSP\",\"Sch. La BSP\",\"Sen. I Sta B\",\"Sen. I Sta A\",\"Sen. I Sta S\"]}"))),
            @ApiResponse(responseCode = "404", description = "Tournament not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"status\":404,\"message\":\"Tournament not found\"}")))
    })
    public LongTournament returnTournament(
            @Parameter(description = "ID of the tournament to be found", required = true) @Validated @PathVariable String tournamentId) {
        logger.info("request at TournamentID: {}", tournamentId);
        if (tournamentId.equals("418"))
            throw new IAmATeapotException();

        return tr.readTournament(Long.valueOf(tournamentId));
    }

    @CrossOrigin
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ EmptyTournamentException.class })
    public ErrorResponse handleEmptyTournamentException(EmptyTournamentException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @CrossOrigin
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler({ IAmATeapotException.class })
    public ErrorResponse handleTeapot() {
        return new ErrorResponse(HttpStatus.I_AM_A_TEAPOT.value(),
                "Unable to provide coffee. Available selection includes Yorkshire Tea, Green Tea and fruit tea");
    }

    // This updates only the Cache of all Tournaments
    @Scheduled(fixedRate = 3600000)
    public void updateCollectionAndMap() {
        logger.info("updating Cache for  all tournaments");
        kr.updateTournaments();
        Collection<ShortTournament> cst = kr.getTournaments();
        for (ShortTournament st : cst)
            tr.readTournament(st.getId());
    }
}
