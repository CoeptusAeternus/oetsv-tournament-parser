package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.IAmATeapotException;
import ch.seiberte.tournamentParser.mailers.ITournamentMailer;
import ch.seiberte.tournamentParser.mailers.NennschlussReminderService;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@RestController
public class Endpoints {

    private ITournamentMailer nennschlussReminder;
    private ITournamentReader tr;
    private IKalenderProxy kr;

    public Endpoints() {
        this.tr = new RenamingTournamentProxy();
        this.kr = new TournamentListProxyWithMailer();
        this.nennschlussReminder = new NennschlussReminderService();
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

    @Scheduled(fixedRate = 3600000)
    public void updateCollectionAndMap() {
        logger.info("updating all tournaments");
        kr.updateTournaments();
        Collection<ShortTournament> cst = kr.getTournaments();
        for (ShortTournament st : cst)
            tr.readTournament(st.getId());
    }

    @Scheduled(cron = "0 0 6 1/1 * ?")
    public void checkForNennschluss(){
        logger.info("checking for Nennschluss Reminders");
        LocalDateTime now = LocalDateTime.now();
        now = now.withSecond(0).withMinute(0).withHour(0).truncatedTo(ChronoUnit.SECONDS);
        for(ShortTournament st : kr.getTournaments()) {
            LocalDateTime nennschlussReminderDate = st.getStart();
            nennschlussReminderDate = nennschlussReminderDate.minusDays(13);
            if (nennschlussReminderDate.equals(now)) {
                logger.info("Sending Nennschluss Reminder Mail for: " + st.getBezeichnung());
                nennschlussReminder.sendMail(st, "jaksei.lol@gmail.com");
                nennschlussReminder.sendMail(st, "sportwart@schwarzgold.at");
                nennschlussReminder.sendMail(st, "fiona.gartlgruber@gmail.com");
            }
        }
    }
}
