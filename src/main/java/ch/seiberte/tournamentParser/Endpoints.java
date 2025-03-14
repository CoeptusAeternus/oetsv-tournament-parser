package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.IAmATeapotException;
import ch.seiberte.tournamentParser.proxys.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@RestController
public class Endpoints {

    private final ITournamentReader tr;
    private final IKalenderProxy kr;

    public Endpoints() {
        this.tr = new RenamingTournamentProxy();
        this.kr = new ListProxy();
    }

    private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

    @CrossOrigin
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all available Tournaments", description = "Returns a list of all available Tournaments")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of Tournaments found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ShortTournament.class)))
            )
    })
    public List<ShortTournament> returnList() {
        logger.info("request at /list");
        return kr.getTournaments();
    }

    @CrossOrigin
    @RequestMapping(value = "/tournament/{tournamentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a specific Tournament", description = "Returns a specific Tournament by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Tournament found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LongTournament.class))),
            @ApiResponse(responseCode = "400", description = "Tournament not found")
    })
    public LongTournament returnTournament(
            @Parameter(description = "ID of the Tournament to be found", required = true)
            @Validated @PathVariable String tournamentId) {
        logger.info("request at TournamentID: {}", tournamentId
        );
        if(tournamentId.equals("418"))
            throw new IAmATeapotException();

        return tr.readTournament(Long.valueOf(tournamentId));
    }

    @CrossOrigin
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EmptyTournamentException.class})
    public Map<String, String> handleEmptyTournamentException(EmptyTournamentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("status", HttpStatus.BAD_REQUEST.value() + "");
        errorMap.put("message", ex.getMessage());
        return errorMap;
    }

    @CrossOrigin
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler({IAmATeapotException.class})
    public Map<String, String> handleTeapot() {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("status", HttpStatus.I_AM_A_TEAPOT.value()+"");
        errorMap.put("message", "Unable to provide Coffee - Available selection includes Yorkshire Tea, Green Tea and fruit tea");
        return errorMap;
    }

    //This updates only the Cache of all Tournaments
    @Scheduled(fixedRate = 3600000)
    public void updateCollectionAndMap() {
        logger.info("updating Cache for  all tournaments");
        kr.updateTournaments();
        Collection<ShortTournament> cst = kr.getTournaments();
        for (ShortTournament st : cst)
            tr.readTournament(st.getId());
    }
}
