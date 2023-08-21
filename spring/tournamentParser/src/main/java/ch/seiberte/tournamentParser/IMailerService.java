package ch.seiberte.tournamentParser;

public interface IMailerService {

    void sendMail(String to, String text, String subject);

}
