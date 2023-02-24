package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.OetsvCalendarDataParser;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.Collection;
import java.util.HashSet;

public class ListProxy implements IKalenderReader {

    private Collection<ShortTournament> currentList =new HashSet<>();
    private final IKalenderReader baseService = new OetsvCalendarDataParser();

    @Override
    public Collection<ShortTournament> getTournaments() {
        if(currentList.isEmpty())
            updateList();
        return currentList;
    }

    private void updateList(){
        currentList = baseService.getTournaments();
    }

}
