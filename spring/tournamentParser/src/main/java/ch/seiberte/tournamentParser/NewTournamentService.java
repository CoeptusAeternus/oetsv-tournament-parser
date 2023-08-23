package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

public class NewTournamentService implements ITournamentMailer{

    IMailerService mailerService;
    public NewTournamentService() {
        this.mailerService = new MailerService();
    }

    @Override
    public void sendMail(ShortTournament st, String email) {

        String subject = "Neues Turnier im ÖTSV-Kalender gefunden: " + st.getBezeichnung();
        String text = "Informationen:\nBezeichnung: "+st.getBezeichnung()+"\nDatum:"+st.getStart().toString()+"\nKalender: https://www.tanzsportverband.at/kalender/";

        mailerService.sendMail(email,text,subject);

    }
}
