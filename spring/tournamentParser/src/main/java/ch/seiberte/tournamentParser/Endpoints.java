package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.IAmATeapotException;
import ch.seiberte.tournamentParser.mailers.ITournamentMailer;
import ch.seiberte.tournamentParser.mailers.NennschlussReminderService;
import ch.seiberte.tournamentParser.proxys.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String mainPage() {
        logger.info("request at /");
        return "<p>api at <a href=/oetsv_kalender title=api>/oetsv_kalender</a></p>";
    }

    @RequestMapping(value = "/oetsv_kalender", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String linkTreePage() {
        logger.info("request at /oetsv_kalender");
        return "Overview List for all current Tournaments under <a href=/oetsv_kalender/list>/list</a><br>Details for single tournament with /&lt;id&gt;";
    }

    @RequestMapping(value = "/oetsv_kalender/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShortTournament> returnList() {
        logger.info("request at /oetsv_kalender/list");
        return kr.getTournaments();
    }

    @RequestMapping(value = "/oetsv_kalender/{tournamentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LongTournament returnTournament(@Validated @PathVariable String tournamentId) {
        logger.info("request at TournamentID: {}", tournamentId);
        if(tournamentId.equals("418"))
            throw new IAmATeapotException();

        return tr.readTournament(Long.valueOf(tournamentId));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EmptyTournamentException.class})
    public Map<String, String> handleEmptyTournamentException(EmptyTournamentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("status", HttpStatus.BAD_REQUEST.value() + "");
        errorMap.put("message", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler({IAmATeapotException.class})
    public Map<String, String> handleTeapot() {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("status", HttpStatus.I_AM_A_TEAPOT.value()+"");
        errorMap.put("message", "Unable to provide Coffee - Available selection includes Yorkshire Tea, Green Tea and fruit tea");
        return errorMap;
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> handleError(HttpServletRequest request) {
        Map<String, String> errorMap = new HashMap<>();

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null)
            errorMap.put("status", status.toString());

        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (uri != null)
            errorMap.put("uri", uri.toString());

        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message != null)
            errorMap.put("message", message.toString());

        logger.warn("Error: {}", errorMap);

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
            }
        }
    }
}
