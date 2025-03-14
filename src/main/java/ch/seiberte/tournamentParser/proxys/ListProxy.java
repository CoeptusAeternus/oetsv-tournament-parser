package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.OetsvCalendarDataParser;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListProxy implements IKalenderProxy {

    private List<ShortTournament> currentList =new ArrayList<>();
    private final IKalenderReader baseService = new OetsvCalendarDataParser();

    @Override
    public List<ShortTournament> getTournaments() {
        return currentList;
    }

    @Override
    public void updateTournaments() {
        currentList = baseService.getTournaments();
        currentList.sort( Comparator.comparing(ShortTournament::getStart) );
    }
}
