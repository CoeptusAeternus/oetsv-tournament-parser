package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.exceptions.EmptyTournamentException;
import ch.seiberte.tournamentParser.exceptions.UnableToReadDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OetsvTournamentDataParser implements ITournamentReader {

    private static final Logger logger = LoggerFactory.getLogger(OetsvTournamentDataParser.class);
    private static final String urlPart1 = "https://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr=";
    private static final String urlPart2 = "&art=IN&conf_html=1";

    @Override
    public LongTournament readTournament(Long id) {

        Document htmlDoc;

        try {
            htmlDoc = Jsoup.connect(urlPart1 + id.toString() + urlPart2).get();
        } catch (IOException e) {
            logger.error("Unable to access URL: "+urlPart1 + id + urlPart2);
            throw new UnableToReadDataException();
        }

        if(htmlDoc.body().text().equals("")) {
            logger.warn("Could not get Data from Tournament with ID: "+id);
            throw new EmptyTournamentException("Tournament not found");
        }
        Element dataBody = htmlDoc.select("table").first().select("tr").get(3);

        String rawAdressString = htmlDoc.select("td").get(8).text();
        String address = rawAdressString.substring(0, rawAdressString.length() - 16);

        String bezeichnung = dataBody.select("td").get(4).text();

        String rawDateTimeString = dataBody.select("td").get(8).text();
        LocalDateTime start = parseDateTimeFromString(rawDateTimeString);

        String nenngeld = parseNenngeld(dataBody.text());

        List<String> klassen = new ArrayList<>();
        Element klassenTable = dataBody.select("td").last();
        Elements klassenElements = klassenTable.getElementsByTag("b");
        for (Element klassenElement : klassenElements) {
            String rawKlassenString = klassenElement.text();
            String klassenString = rawKlassenString.substring(0, rawKlassenString.length() - 5);
            klassen.add(klassenString);
        }


        return new LongTournament(address, bezeichnung, start, id, nenngeld, klassen);
    }

    private LocalDateTime parseDateTimeFromString(String dateTime) {

        String rawDateString = dateTime.substring(5, dateTime.length() - 4);

        DateTimeFormatter oetsvDateTimePattern = DateTimeFormatter.ofPattern("d. M. yyyy, H:mm");

        return LocalDateTime.parse(rawDateString, oetsvDateTimePattern);
    }

    private String parseNenngeld(String data) {
        Pattern nenngeldSearchPattern = Pattern.compile("Nenngeld:?(\\s|\\w|€)*\\d+(,(\\d{2}|-)|)");
        Matcher nenngeldMatcher = nenngeldSearchPattern.matcher(data);

        if (nenngeldMatcher.find()) {

            String nenngeldFound = nenngeldMatcher.group(0);
            Pattern nenngeldAmountPattern = Pattern.compile("\\d+(,(\\d{2}|-)|)");
            Matcher nenngeldAmountMatcher = nenngeldAmountPattern.matcher(nenngeldFound);

            if (nenngeldAmountMatcher.find())
                return nenngeldAmountMatcher.group(0);
            else {
                logger.info("Could not process Nenngeld in String: "+data);
                return "Nenngeld konnte nicht verarbeitet werden. Bitte in Ausschreibung nachlesen";
            }

        } else
            return "0";

    }
}
