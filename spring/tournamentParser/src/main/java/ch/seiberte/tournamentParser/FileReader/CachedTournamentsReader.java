package ch.seiberte.tournamentParser.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CachedTournamentsReader {

    private static final Logger logger = LoggerFactory.getLogger(CachedTournamentsReader.class);
    private final static String path = "/var/tournamentParser/notified.txt";

    private void createFileIfNotExists() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                boolean folder_created = file.getParentFile().mkdirs();
                boolean file_created = file.createNewFile();
                if (!folder_created || !file_created) {
                    logger.error("Error creating caching File");
                }
            } catch (Exception e) {
                logger.error("Error creating caching File: " + e.getMessage());

            }
        }
    }



    public boolean isNotified(Long tournamentId) {

        Path p = Path.of(path);

        String contents = null;
        try {
            contents = Files.readString(p);
        } catch (IOException e) {
            createFileIfNotExists();
            contents = "";
        }

        contents = StringUtils.trimTrailingCharacter(contents, ',');

        String[] lines = contents.split(",");

        return Arrays.asList(lines).contains(tournamentId.toString());
    }

    public void addNotified(Long tournamentId) {
        createFileIfNotExists();

        File f = new File(path);
        try {
            String contents = Files.readString(f.toPath());
            contents += tournamentId + ",";
            Files.writeString(f.toPath(), contents);
        } catch (IOException e) {
            logger.error("Error writing to caching File: " + e.getMessage());
        }

    }

    public boolean fileIsEmpty() {
        createFileIfNotExists();
        File f = new File(path);
        return f.length() == 0;
    }

}
