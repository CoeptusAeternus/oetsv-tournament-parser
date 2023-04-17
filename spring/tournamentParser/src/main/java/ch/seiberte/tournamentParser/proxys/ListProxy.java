package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.OetsvCalendarDataParser;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.ArrayList;
import java.util.List;

public class ListProxy implements IKalenderReader {

    private List<ShortTournament> currentList =new ArrayList<>();
    private final IKalenderReader baseService = new OetsvCalendarDataParser();

    @Override
    public List<ShortTournament> getTournaments() {
        updateList();
        currentList.sort((o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? -1 : 1);
        return currentList;
    }

    private void updateList(){
        currentList = baseService.getTournaments();
    }

}
