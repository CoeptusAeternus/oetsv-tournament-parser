package ch.seiberte.tournamentParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.UnableToReadDataException;

public class OetsvTournamentDataParser implements ITournamentReader {

    private static final Logger logger = LoggerFactory.getLogger(OetsvTournamentDataParser.class);
    private static final String urlPart1 = "https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr=";
    private static final String urlPart2 = "&art=IN&conf_html=1";

    @Override
    public LongTournament readTournament(Long id) throws UnableToReadDataException {

        Document htmlDoc;

        try {
            htmlDoc = Jsoup.connect(urlPart1 + id.toString() + urlPart2).get();
        } catch (IOException e) {
            logger.error("Unable to access URL: {}", (urlPart1 + id + urlPart2) );
            throw new UnableToReadDataException();
        }
        return parseTournamentFromDoc(htmlDoc, id);
    }

    LongTournament parseTournamentFromDoc(Document htmlDoc, Long id) throws UnableToReadDataException {

        if(htmlDoc.body().text().isEmpty()) {
            logger.warn("Could not get Data from Tournament with ID: {}", id);
            throw new EmptyTournamentException("Tournament not found");
        }

        Optional<Element> table = Optional.ofNullable(htmlDoc.select("table").first());
        if( table.isEmpty() )
            throw new UnableToReadDataException("No table found");

        Optional<Element> dataBody = Optional.ofNullable(table.get().select("tr").get(3));
        if( dataBody.isEmpty() )
            throw new UnableToReadDataException("Do Table Row found at index 3");

        String rawAdressString = htmlDoc.select("td").get(8).text();
        String address = rawAdressString.substring(0, rawAdressString.length() - 16);

        String name = dataBody.get().select("td").get(4).text();

        String rawDateTimeString = dataBody.get().select("td").get(8).text();
        LocalDateTime start = parseDateTimeFromString(rawDateTimeString);

        String fee = parseFee(dataBody.get().text());

        List<String> klassen = new ArrayList<>();

        Optional<Element> klassenTable = Optional.ofNullable(dataBody.get().select("td").last());

        if( klassenTable.isPresent()){

            Elements klassenElements = klassenTable.get().getElementsByTag("b");

            for (Element klassenElement : klassenElements) {
                String rawKlassenString = klassenElement.text();
                String klassenString = rawKlassenString.substring(0, rawKlassenString.length() - 5);
                klassen.add(klassenString);
            }

        }


        return new LongTournament(address, name, start, id, fee, klassen);
    }

    private LocalDateTime parseDateTimeFromString(String dateTime) {

        String rawDateString = dateTime.substring(5, dateTime.length() - 4);

        DateTimeFormatter oetsvDateTimePattern = DateTimeFormatter.ofPattern("d. M. yyyy, H:mm");

        return LocalDateTime.parse(rawDateString, oetsvDateTimePattern);
    }

    private String parseFee(String data) {
        Pattern feeSearchPattern = Pattern.compile("Nenngeld:?(\\s|\\w|€)*\\d+(,(\\d{2}|-)|)");
        Matcher feeMatcher = feeSearchPattern.matcher(data);

        if (feeMatcher.find()) {

            String feeFound = feeMatcher.group(0);
            Pattern feeAmountPattern = Pattern.compile("\\d+(,(\\d{2}|-)|)");
            Matcher feeAmountMatcher = feeAmountPattern.matcher(feeFound);

            if (feeAmountMatcher.find())
                return feeAmountMatcher.group(0);
            else {
                logger.info("Could not process fee in String: {}", data);
                return "Fee could not be parsed. Please check the event listing.";
            }

        } else
            return "0";

    }
}
