package ch.seiberte.tournamentParser.mailers;

public interface IMailerService {

    void sendMail(String to, String text, String subject);

}
