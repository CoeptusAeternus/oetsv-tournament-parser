package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OetsvCalendarDataParser implements IKalenderReader {
    //TODO add logging and error handeling
    private static final String urlString = "https://www.tanzsportverband.at/kalender/daten.html";

    @Override
    public Collection<ShortTournament> getTournaments() {

        Document htmlDoc;
        try {
            htmlDoc = Jsoup.connect(urlString).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return praseTournamentsFromDoc(htmlDoc);
    }


    private Collection<ShortTournament> praseTournamentsFromDoc(Document doc) {
        Collection<ShortTournament> tournamentSet = new HashSet<>();

        Element table = doc.select("table").first();
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) {//skip first row as it contains names for columns
            Element currentRow = rows.get(i);
            Elements columns = currentRow.select("td");


            Elements links = columns.get(4).getElementsByTag("a");//verify Tournament has a Ausschreibung
            if (links.size() != 0)
                if (links.first().text().equals("Ausschreibung")) {
                    Element date = columns.get(0);
                    String dateString = date.text();
                    DateTimeFormatter europeanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy");
                    LocalDate localDate = LocalDate.parse(dateString,europeanFormatter);

                    Element name = columns.get(1).getElementsByTag("b").first();
                    String nameString = name.text();

                    Pattern idPattern = Pattern.compile("TKNr=\\d+&");
                    Matcher idMatcher = idPattern.matcher(links.first().attr("href"));
                    String tknr ="";
                    if (idMatcher.find())
                         tknr = idMatcher.group(0);

                    String id= tknr.substring(5,tknr.length()-1);

                    ShortTournament st = new ShortTournament(Long.valueOf(id),nameString,localDate);

                    tournamentSet.add(st);
                }
        }

        return tournamentSet;
    }




}
