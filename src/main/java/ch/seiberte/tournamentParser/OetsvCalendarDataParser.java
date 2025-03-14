package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.exceptions.UnableToReadDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OetsvCalendarDataParser implements IKalenderReader {
    //TODO add logging and error handling
    private static final String urlString = "https://www.tanzsportverband.at/kalender/daten.html";

    @Override
    public List<ShortTournament> getTournaments() {

        Document htmlDoc;
        try {
            htmlDoc = Jsoup.connect(urlString).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(parseTournamentsFromDoc(htmlDoc));
    }


    private Collection<ShortTournament> parseTournamentsFromDoc(Document doc) throws UnableToReadDataException {
        Collection<ShortTournament> tournamentSet = new HashSet<>();

        Optional<Element> table = Optional.ofNullable(doc.select("table").first());
        if( table.isEmpty() )
            throw new UnableToReadDataException("No table found");

        Elements rows = table.get().select("tr");

        for (int i = 1; i < rows.size(); i++) {//skip first row as it contains names for columns
            Element currentRow = rows.get(i);
            Elements columns = currentRow.select("td");


            Elements links = columns.get(4).getElementsByTag("a");//verify Tournament has a Ausschreibung
            if (!links.isEmpty()) {
                Optional<Element> link = Optional.ofNullable(links.first());

                if (link.isPresent() && link.get().text().equals("Ausschreibung")) {
                    Optional<Element> date = Optional.ofNullable(columns.first());
                    if( date.isEmpty() )
                        continue;

                    String dateString = date.get().text();
                    DateTimeFormatter europeanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy");
                    LocalDate localDate = LocalDate.parse(dateString, europeanFormatter);

                    Optional<Element> name = Optional.ofNullable(columns.get(1).getElementsByTag("b").first());
                    if( name.isEmpty() )
                        continue;

                    String nameString = name.get().text();

                    Pattern idPattern = Pattern.compile("TKNr=\\d+&");
                    Matcher idMatcher = idPattern.matcher(link.get().attr("href"));
                    String tknr = "";
                    if (idMatcher.find())
                        tknr = idMatcher.group(0);

                    String id = tknr.substring(5, tknr.length() - 1);

                    ShortTournament st = new ShortTournament(Long.valueOf(id), nameString, localDate);

                    tournamentSet.add(st);
                }
            }
        }

        return tournamentSet;
    }




}
