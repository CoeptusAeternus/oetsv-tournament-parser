package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.data.KalenderParser;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ListProxy implements IKalenderReader {

    private Map<Long, ShortTournament> currentList;
    private IKalenderReader baseService;

    public ListProxy() {
        this.currentList=new HashMap<>();
        this.baseService=new KalenderParser();//TODO
    }

    @Override
    public Collection<ShortTournament> getTournaments() {
        return currentList.values();
    }

    public Collection<Long> getCurrentIds(){
        return currentList.keySet();
    }
    public void updateList(){

    }

}
